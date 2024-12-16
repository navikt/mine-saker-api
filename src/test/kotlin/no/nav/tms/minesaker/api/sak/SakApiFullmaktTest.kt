package no.nav.tms.minesaker.api.sak

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
import io.ktor.util.*
import io.ktor.utils.io.*
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.tms.minesaker.api.SakService
import no.nav.tms.minesaker.api.SubstantialAuth
import no.nav.tms.minesaker.api.setup.jsonConfig
import no.nav.tms.minesaker.api.mineSakerApi
import no.nav.tms.minesaker.api.setup.SafResultException
import no.nav.tms.minesaker.api.saf.fullmakt.*
import no.nav.tms.minesaker.api.saf.journalposter.v1.*
import no.nav.tms.minesaker.api.saf.sakstemaer.ForenkletSakstema
import no.nav.tms.minesaker.api.saf.sakstemaer.Kildetype
import no.nav.tms.minesaker.api.saf.sakstemaer.SakstemaResult
import no.nav.tms.minesaker.api.saf.sakstemaer.Sakstemakode
import no.nav.tms.token.support.idporten.sidecar.mock.LevelOfAssurance
import no.nav.tms.token.support.idporten.sidecar.mock.idPortenMock
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class SakApiFullmaktTest {
    private val sakService: SakService = mockk()
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
    fun `henter sakstema for innlogget bruker hvis fullmaktsgiver ikke er satt`() = sakApiFullmaktTest { client ->

        coEvery {
            sakService.hentSakstemaer(any())
        } returns sakstemaResponse(Sakstemakode.AAP)

        coEvery {
            sakService.hentSakstemaer(any(), fullmaktGiver1.ident)
        } returns sakstemaResponse(Sakstemakode.YRK)

        val response: List<ForenkletSakstema> = client.get("sakstemaer").body()

        response.first().kode shouldBe Sakstemakode.AAP
    }

    @Test
    fun `henter sakstema for fullmaktsgiver hvis den er satt for session`() = sakApiFullmaktTest { client ->

        sessionStore.setFullmaktGiver(ident, fullmaktGiver1)

        coEvery {
            sakService.hentSakstemaer(any())
        } returns sakstemaResponse(Sakstemakode.AAP)

        coEvery {
            sakService.hentSakstemaer(any(), fullmaktGiver1.ident)
        } returns sakstemaResponse(Sakstemakode.YRK)

        val response: List<ForenkletSakstema> = client.get("sakstemaer").body()

        response.first().kode shouldBe Sakstemakode.YRK
    }

    @Test
    fun `henter journalposter for innlogget bruker hvis fullmaktsgiver ikke er satt`() = sakApiFullmaktTest { client ->

        val navnForBruker = "Tilhører bruker"
        val navnForRepresentert = "Tilhører representert"

        coEvery {
            sakService.hentJournalposterForSakstema(any(), Sakstemakode.AAP)
        } returns journalpostResponse(navnForBruker)

        coEvery {
            sakService.hentJournalposterForSakstema(any(), Sakstemakode.AAP, fullmaktGiver1.ident)
        } returns journalpostResponse(navnForRepresentert)

        val responseForQuery: List<JournalposterResponse> = client.get("journalposter?sakstemakode=AAP").body()
        val responseForPathParam: List<JournalposterResponse> = client.get("journalposter/AAP").body()

        responseForQuery.first().navn shouldBe navnForBruker
        responseForPathParam.first().navn shouldBe navnForBruker
    }

    @Test
    fun `henter journalposter for fullmaktsgiver hvis den er satt`() = sakApiFullmaktTest { client ->
        val navnForBruker = "Tilhører bruker"
        val navnForRepresentert = "Tilhører representert"

        sessionStore.setFullmaktGiver(ident, fullmaktGiver1)

        coEvery {
            sakService.hentJournalposterForSakstema(any(), Sakstemakode.AAP)
        } returns journalpostResponse(navnForBruker)

        coEvery {
            sakService.hentJournalposterForSakstema(any(), Sakstemakode.AAP, fullmaktGiver1.ident)
        } returns journalpostResponse(navnForRepresentert)

        val responseForQuery: List<JournalposterResponse> = client.get("journalposter?sakstemakode=AAP").body()
        val responseForPathParam: List<JournalposterResponse> = client.get("journalposter/AAP").body()

        responseForQuery.first().navn shouldBe navnForRepresentert
        responseForPathParam.first().navn shouldBe navnForRepresentert
    }

    @Test
    fun `nullstiller fullmakt-sesjon dersom saf returnerer feil for sakstemaer`() = sakApiFullmaktTest { client ->
        sessionStore.setFullmaktGiver(ident, fullmaktGiver1)

        coEvery {
            sakService.hentSakstemaer(any())
        } returns sakstemaResponse(Sakstemakode.AAP)

        coEvery {
            sakService.hentSakstemaer(any(), fullmaktGiver1.ident)
        } throws SafResultException("Error", emptyList(), emptyMap())

        val firstResponse = client.get("sakstemaer")
        val secondResponse = client.get("sakstemaer")

        firstResponse.status shouldBe HttpStatusCode.InternalServerError
        secondResponse.status shouldBe HttpStatusCode.OK

        secondResponse.body<List<ForenkletSakstema>>().first().kode shouldBe Sakstemakode.AAP

        sessionStore.getCurrentFullmaktGiver(ident) shouldBe null
    }


    @Test
    fun `nullstiller fullmakt-sesjon dersom saf returnerer feil for journalposter`() = sakApiFullmaktTest { client ->
        val navnForBruker = "Tilhører bruker"

        sessionStore.setFullmaktGiver(ident, fullmaktGiver1)

        coEvery {
            sakService.hentJournalposterForSakstema(any(), Sakstemakode.AAP)
        } returns journalpostResponse(navnForBruker)

        coEvery {
            sakService.hentJournalposterForSakstema(any(), Sakstemakode.AAP, fullmaktGiver1.ident)
        } throws SafResultException("Error", emptyList(), emptyMap())

        val firstResponse = client.get("journalposter/AAP")
        val secondResponse = client.get("journalposter/AAP")

        firstResponse.status shouldBe HttpStatusCode.InternalServerError
        secondResponse.status shouldBe HttpStatusCode.OK

        secondResponse.body<List<JournalposterResponse>>().first().navn shouldBe navnForBruker

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
                sakService = sakService,
                httpClient = testClient,
                corsAllowedOrigins = "*",
                sakerUrl = "N/A",
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

                        idPortenMock {
                            authenticatorName = SubstantialAuth
                            alwaysAuthenticated = true
                            setAsDefault = false
                            staticLevelOfAssurance = LevelOfAssurance.SUBSTANTIAL
                            staticUserPid = ident
                        }
                    }
                }
            )
        }

        testBlock(testClient)
    }

    private fun sakstemaResponse(sakstema: Sakstemakode) = SakstemaResult(
        listOf(
            ForenkletSakstema(
                navn = sakstema.name,
                kode = sakstema,
                sistEndret = ZonedDateTime.now(),
                detaljvisningUrl = "https://link",
            )
        ),
        listOf(Kildetype.SAF)
    )

    private fun journalpostResponse(navn: String, temakode: Sakstemakode = Sakstemakode.AAP) =
        JournalposterResponse(
            temanavn = navn,
            temakode = temakode,
            journalposter = listOf(
                Journalpost(
                    tittel = "Tittel",
                    journalpostId = "123",
                    journalposttype = Journalposttype.NOTAT,
                    avsender = null,
                    mottaker = null,
                    sisteEndret = ZonedDateTime.now(),
                    dokumenter = emptyList(),
                    harVedlegg = false,
                )
            )
        )

}
