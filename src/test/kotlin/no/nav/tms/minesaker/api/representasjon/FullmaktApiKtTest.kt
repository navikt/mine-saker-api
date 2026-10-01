package no.nav.tms.minesaker.api.representasjon

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
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.tms.minesaker.api.setup.jsonConfig
import no.nav.tms.minesaker.api.mineSakerApi
import no.nav.tms.token.support.user.token.verification.Issuer
import no.nav.tms.token.support.user.token.verification.LevelOfAssurance
import no.nav.tms.token.support.user.token.verificaton.mock.userTokenMock
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class FullmaktApiKtTest {
    private val reprService: ReprService = mockk()
    private val sessionStore = ReprTestSessionStore()

    private val ident = "123"
    private val navn = "Innlogget Bruker"

    private val representert1 = Representert("111", "abc", type = Representasjonstype.Fullmakt)
    private val representert2 = Representert("222", "def", type = Representasjonstype.Fullmakt)

    private val fullmaktGivere = listOf(representert1, representert2)

    @AfterEach
    fun cleanUp() = runBlocking{
        sessionStore.clearRepresentert(ident)
    }

    @Test
    fun `henter info om fullmakt for sesjon`() = fullmaktApiTest {

        client.get("/fullmakt/info").validateResponse { json ->
            json["viserRepresentertesData"].asBoolean() shouldBe false
            json["representertIdent"].isNull shouldBe true
            json["representertNavn"].isNull shouldBe true
        }

        sessionStore.setRepresentert(ident, representert1)

        client.get("/fullmakt/info").validateResponse { json ->
            json["viserRepresentertesData"].asBoolean() shouldBe true
            json["representertIdent"].asText() shouldBe representert1.ident
            json["representertNavn"].asText() shouldBe representert1.navn
        }

        sessionStore.setRepresentert(ident, representert2)

        client.get("/fullmakt/info").validateResponse { json ->
            json["viserRepresentertesData"].asBoolean() shouldBe true
            json["representertIdent"].asText() shouldBe representert2.ident
            json["representertNavn"].asText() shouldBe representert2.navn
        }
    }

    @Test
    fun `henter gjeldende forhold for bruker`() = fullmaktApiTest {
        coEvery {
            reprService.getFullmaktForhold(any())
        } returns Representantforhold(
            navn = navn,
            ident = ident,
            fullmaktsGivere = emptyList(),
            verger = emptyList()
        )

        client.get("/fullmakt/forhold").validateResponse { json ->
            json["ident"].asText() shouldBe ident
            json["navn"].asText() shouldBe navn
            json["fullmaktsGivere"].size() shouldBe 0
        }

        coEvery {
            reprService.getFullmaktForhold(any())
        } returns Representantforhold(
            navn = navn,
            ident = ident,
            fullmaktsGivere = fullmaktGivere,
            verger = emptyList()
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
            reprService.validateForhold(any(), representert1.ident, any())
        } returns representert1

        client.post("/fullmakt/representert") {
            setBody("""{"ident": "${representert1.ident}"}""")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }

        sessionStore.getCurrentRepresentert(ident).let { giver ->
            giver.shouldNotBeNull()
            giver.ident shouldBe representert1.ident
            giver.navn shouldBe representert1.navn
        }
    }

    @Test
    fun `svarer med feil dersom en setter aktivt forhold som ikke er gyldig`() = fullmaktApiTest {
        coEvery {
            reprService.validateForhold(any(), representert1.ident, any())
        } throws UgyldigFullmaktException("Ugyldig", representert1.ident, ident)

        val response = client.post("/fullmakt/representert") {
            setBody("""{"ident": "${representert1.ident}"}""")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }

        response.status shouldBe HttpStatusCode.Forbidden

        sessionStore.getCurrentRepresentert(ident).shouldBeNull()
    }

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
                reprService = reprService,
                reprSessionStore = sessionStore,
                authConfig = {
                    authentication {
                        userTokenMock {
                            levelOfAssurance = LevelOfAssurance.High
                            enableDefaultAuthentication {
                                tokenIssuer = Issuer.IdPorten
                                tokenIdent = ident
                            }
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
