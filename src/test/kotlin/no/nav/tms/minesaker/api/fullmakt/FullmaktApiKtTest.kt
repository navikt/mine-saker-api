package no.nav.tms.minesaker.api.fullmakt

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.auth.*
import io.ktor.server.testing.*
import io.ktor.utils.io.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.tms.minesaker.api.setup.jsonConfig
import no.nav.tms.minesaker.api.mineSakerApi
import no.nav.tms.token.support.idporten.sidecar.mock.LevelOfAssurance
import no.nav.tms.token.support.idporten.sidecar.mock.idPortenMock
import no.nav.tms.token.support.tokenx.validation.mock.tokenXMock
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class FullmaktApiKtTest {
    private val fullmaktService: FullmaktService = mockk()
    private val sessionStore = FullmaktTestSessionStore()

    private val ident = "123"
    private val navn = "Innlogget Bruker"

    private val fullmaktGiver1 = FullmaktGiver("111", "abc")
    private val fullmaktGiver2 = FullmaktGiver("222", "def")

    private val fullmaktGivere = listOf(fullmaktGiver1, fullmaktGiver2)

    @AfterEach
    fun cleanUp() = runBlocking{
        sessionStore.clearFullmaktGiver(ident)
    }

    @Test
    fun `henter info om fullmakt for sesjon`() = fullmaktApiTest {

        client.get("/fullmakt/info").validateResponse { json ->
            json["viserRepresentertesData"].asBoolean() shouldBe false
            json["representertIdent"].isNull shouldBe true
            json["representertNavn"].isNull shouldBe true
        }

        sessionStore.setFullmaktGiver(ident, fullmaktGiver1)

        client.get("/fullmakt/info").validateResponse { json ->
            json["viserRepresentertesData"].asBoolean() shouldBe true
            json["representertIdent"].asText() shouldBe fullmaktGiver1.ident
            json["representertNavn"].asText() shouldBe fullmaktGiver1.navn
        }

        sessionStore.setFullmaktGiver(ident, fullmaktGiver2)

        client.get("/fullmakt/info").validateResponse { json ->
            json["viserRepresentertesData"].asBoolean() shouldBe true
            json["representertIdent"].asText() shouldBe fullmaktGiver2.ident
            json["representertNavn"].asText() shouldBe fullmaktGiver2.navn
        }
    }

    @Test
    fun `henter gjeldende forhold for bruker`() = fullmaktApiTest {
        coEvery {
            fullmaktService.getFullmaktForhold(any())
        } returns FullmaktForhold(
            navn = navn,
            ident = ident,
            fullmaktsGivere = emptyList()
        )

        client.get("/fullmakt/forhold").validateResponse { json ->
            json["ident"].asText() shouldBe ident
            json["navn"].asText() shouldBe navn
            json["fullmaktsGivere"].size() shouldBe 0
        }

        coEvery {
            fullmaktService.getFullmaktForhold(any())
        } returns FullmaktForhold(
            navn = navn,
            ident = ident,
            fullmaktsGivere = fullmaktGivere
        )

        client.get("/fullmakt/forhold").validateResponse { json ->
            json["ident"].asText() shouldBe ident
            json["navn"].asText() shouldBe navn
            json["fullmaktsGivere"].size() shouldBe fullmaktGivere.size
        }
    }

    @Test
    fun `setter fullmaktsgiver for sesjon hvis forhold er gyldig`() = fullmaktApiTest {
        coEvery {
            fullmaktService.validateFullmaktsGiver(any(), fullmaktGiver1.ident)
        } returns fullmaktGiver1

        client.post("/fullmakt/representert") {
            setBody("""{"ident": "${fullmaktGiver1.ident}"}""")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }

        sessionStore.getCurrentFullmaktGiver(ident).let { giver ->
            giver.shouldNotBeNull()
            giver.ident shouldBe fullmaktGiver1.ident
            giver.navn shouldBe fullmaktGiver1.navn
        }
    }

    @Test
    fun `svarer med feil dersom en setter aktivt forhold som ikke er gyldig`() = fullmaktApiTest {
        coEvery {
            fullmaktService.validateFullmaktsGiver(any(), fullmaktGiver1.ident)
        } throws UgyldigFullmaktException("Ugyldig", fullmaktGiver1.ident, ident)

        val response = client.post("/fullmakt/representert") {
            setBody("""{"ident": "${fullmaktGiver1.ident}"}""")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }

        response.status shouldBe HttpStatusCode.Forbidden

        sessionStore.getCurrentFullmaktGiver(ident).shouldBeNull()
    }

    @KtorDsl
    private fun fullmaktApiTest(testBlock: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        val testClient = createClient {
            install(ContentNegotiation) {
                jackson {
                    jsonConfig()
                }
            }
            install(HttpTimeout)
        }

        application {
            mineSakerApi(
                safService = mockk(),
                digiSosConsumer = mockk(),
                httpClient = testClient,
                corsAllowedOrigins = "*",
                fullmaktService = fullmaktService,
                fullmaktSessionStore = sessionStore,
                authConfig = {
                    authentication {
                        idPortenMock {
                            alwaysAuthenticated = true
                            setAsDefault = true
                            staticLevelOfAssurance = LevelOfAssurance.HIGH
                            staticUserPid = ident
                        }

                        tokenXMock {
                            alwaysAuthenticated = true
                            setAsDefault = false
                            staticLevelOfAssurance = no.nav.tms.token.support.tokenx.validation.mock.LevelOfAssurance.HIGH
                            staticUserPid = ident
                        }
                    }
                }
            )
        }

        testBlock()
    }

    private val objectMapper = jacksonObjectMapper()

    private suspend fun HttpResponse.validateResponse(validator: (JsonNode) -> Unit) =
        bodyAsText()
            .let { objectMapper.readTree(it) }
            .let { validator(it) }

}
