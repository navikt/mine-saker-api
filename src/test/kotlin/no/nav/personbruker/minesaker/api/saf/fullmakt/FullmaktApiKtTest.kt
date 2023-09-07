package no.nav.personbruker.minesaker.api.saf.fullmakt

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.testing.*
import io.ktor.util.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.minesaker.api.config.jsonConfig
import no.nav.personbruker.minesaker.api.config.mineSakerApi
import no.nav.tms.token.support.idporten.sidecar.mock.LevelOfAssurance
import no.nav.tms.token.support.idporten.sidecar.mock.installIdPortenAuthMock
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
    fun `henter info om fullmakt for sesjon`() = fullmaktApiTest { client ->

        client.get("/fullmakt/info").body<FullmaktInfo>().let { info ->
            info.viserRepresentertesData shouldBe false
            info.representertIdent shouldBe null
            info.representertNavn shouldBe null
        }

        sessionStore.setFullmaktGiver(ident, fullmaktGiver1)

        client.get("/fullmakt/info").body<FullmaktInfo>().let { info ->
            info.viserRepresentertesData shouldBe true
            info.representertIdent shouldBe fullmaktGiver1.ident
            info.representertNavn shouldBe fullmaktGiver1.navn
        }

        sessionStore.setFullmaktGiver(ident, fullmaktGiver2)

        client.get("/fullmakt/info").body<FullmaktInfo>().let { info ->
            info.viserRepresentertesData shouldBe true
            info.representertIdent shouldBe fullmaktGiver2.ident
            info.representertNavn shouldBe fullmaktGiver2.navn
        }
    }

    @Test
    fun `henter gjeldende forhold for bruker`() = fullmaktApiTest { client ->
        coEvery {
            fullmaktService.getFullmaktForhold(any())
        } returns FullmaktForhold(
            navn = navn,
            ident = ident,
            fullmaktsGivere = emptyList()
        )

        client.get("/fullmakt/forhold").body<FullmaktForhold>().let { forhold ->
            forhold.ident shouldBe ident
            forhold.navn shouldBe navn
            forhold.fullmaktsGivere.shouldBeEmpty()
        }

        coEvery {
            fullmaktService.getFullmaktForhold(any())
        } returns FullmaktForhold(
            navn = navn,
            ident = ident,
            fullmaktsGivere = fullmaktGivere
        )

        client.get("/fullmakt/forhold").body<FullmaktForhold>().let { forhold ->
            forhold.ident shouldBe ident
            forhold.navn shouldBe navn
            forhold.fullmaktsGivere shouldContainAll fullmaktGivere
        }
    }

    @Test
    fun `setter fullmaktsgiver for sesjon hvis forhold er gyldig`() = fullmaktApiTest { client ->
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
    fun `svarer med feil dersom en setter aktivt forhold som ikke er gyldig`() = fullmaktApiTest { client ->
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
    private fun fullmaktApiTest(testBlock: suspend (HttpClient) -> Unit) = testApplication {
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
                sakService = mockk(),
                httpClient = testClient,
                corsAllowedOrigins = "*",
                corsAllowedSchemes = "*",
                sakerUrl = "N/A",
                fullmaktService = fullmaktService,
                fullmaktSessionStore = sessionStore,
                authConfig = {
                    installIdPortenAuthMock {
                        setAsDefault = true
                        alwaysAuthenticated = true
                        staticLevelOfAssurance = LevelOfAssurance.HIGH
                        staticUserPid = ident
                    }
                }
            )
        }

        testBlock(testClient)
    }
}
