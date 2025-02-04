package no.nav.tms.minesaker.api.fullmakt

import io.kotest.matchers.shouldBe
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.auth.*
import io.ktor.server.testing.*
import io.ktor.utils.io.*
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.tms.minesaker.api.journalpost.SafService
import no.nav.tms.minesaker.api.journalpost.DokumentHeader
import no.nav.tms.minesaker.api.journalpost.Journalpost
import no.nav.tms.minesaker.api.journalpost.Sakstema
import no.nav.tms.minesaker.api.mineSakerApi
import no.nav.tms.minesaker.api.setup.SafResultException
import no.nav.tms.minesaker.api.setup.jsonConfig
import no.nav.tms.token.support.idporten.sidecar.mock.LevelOfAssurance
import no.nav.tms.token.support.idporten.sidecar.mock.idPortenMock
import no.nav.tms.token.support.tokenx.validation.mock.tokenXMock
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class JournalposterFullmaktTest {
    private val sakService: SafService = mockk()
    private val navnService: NavnFetcher = mockk()
    private val fullmaktConsumer: FullmaktConsumer = mockk()

    private val sessionStore = FullmaktTestSessionStore()
    private val fullmaktService = FullmaktService(fullmaktConsumer, navnService)

    private val ident = "123"

    private val fullmaktGiver1 = FullmaktGiver("111", "abc")


    @AfterEach
    fun cleanUp() = runBlocking {
        clearMocks(sakService, navnService, fullmaktConsumer)
        sessionStore.clearFullmaktGiver(ident)
    }

    @Test
    fun `henter journalposter for innlogget bruker hvis fullmaktsgiver ikke er satt`() = sakApiFullmaktTest { client ->

        val navnForBruker = "Tilhører bruker"
        val navnForRepresentert = "Tilhører representert"

        coEvery {
            sakService.alleJournalposter(any(), null)
        } returns journalpostResponse(navnForBruker)

        coEvery {
            sakService.alleJournalposter(any(), fullmaktGiver1.ident)
        } returns journalpostResponse(navnForRepresentert)

        val responseForQuery: List<Journalpost> = client.get("v2/journalposter/alle").body()

        responseForQuery.first().temanavn shouldBe navnForBruker
    }

    @Test
    fun `henter journalposter for fullmaktsgiver hvis den er satt`() = sakApiFullmaktTest { client ->
        val navnForBruker = "Tilhører bruker"
        val navnForRepresentert = "Tilhører representert"

        sessionStore.setFullmaktGiver(ident, fullmaktGiver1)

        coEvery {
            sakService.alleJournalposter(any(), null)
        } returns journalpostResponse(navnForBruker)

        coEvery {
            sakService.alleJournalposter(any(), fullmaktGiver1.ident)
        } returns journalpostResponse(navnForRepresentert)

        val responseForQuery: List<Journalpost> = client.get("v2/journalposter/alle").body()

        responseForQuery.first().temanavn shouldBe navnForRepresentert
    }


    @Test
    fun `nullstiller fullmakt-sesjon dersom saf returnerer feil for journalposter`() = sakApiFullmaktTest { client ->
        val navnForBruker = "Tilhører bruker"

        sessionStore.setFullmaktGiver(ident, fullmaktGiver1)

        coEvery {
            sakService.alleJournalposter(any(), null)
        } returns journalpostResponse(navnForBruker)

        coEvery {
            sakService.alleJournalposter(any(), fullmaktGiver1.ident)
        } throws SafResultException("Error", emptyList(), emptyMap())

        val firstResponse = client.get("v2/journalposter/alle")
        val secondResponse = client.get("v2/journalposter/alle")

        firstResponse.status shouldBe HttpStatusCode.InternalServerError
        secondResponse.status shouldBe HttpStatusCode.OK

        secondResponse.body<List<Journalpost>>().first().temanavn shouldBe navnForBruker

        sessionStore.getCurrentFullmaktGiver(ident) shouldBe null
    }

    @KtorDsl
    private fun sakApiFullmaktTest(testBlock: suspend (HttpClient) -> Unit) = testApplication {
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
                safService = sakService,
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

        testBlock(testClient)
    }

    private fun journalpostResponse(navn: String, temakode: Sakstema = Sakstema.AAP) =
        listOf(
            Journalpost(
                temanavn = navn,
                temakode = temakode.name,
                tittel = "Tittel",
                journalpostId = "123",
                journalposttype = "Notat",
                avsender = null,
                mottaker = null,
                dokument = DokumentHeader.blank(),
                vedlegg = emptyList(),
                opprettet = ZonedDateTime.now()
            )
        )

}
