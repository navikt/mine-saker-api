package no.nav.personbruker.minesaker.api.fullmakt

import io.kotest.matchers.shouldBe
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.jackson.*
import io.ktor.server.testing.*
import io.ktor.util.*
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import no.nav.personbruker.minesaker.api.config.jsonConfig
import no.nav.personbruker.minesaker.api.config.mineSakerApi
import no.nav.personbruker.minesaker.api.domain.*
import no.nav.personbruker.minesaker.api.domain.Journalposttype.NOTAT
import no.nav.personbruker.minesaker.api.saf.fullmakt.*
import no.nav.personbruker.minesaker.api.sak.Kildetype
import no.nav.personbruker.minesaker.api.sak.SakService
import no.nav.personbruker.minesaker.api.sak.SakstemaResult
import no.nav.tms.token.support.idporten.sidecar.mock.LevelOfAssurance
import no.nav.tms.token.support.idporten.sidecar.mock.installIdPortenAuthMock
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class SakApiFullmaktTest {
    private val sakService: SakService = mockk()
    private val redisService: FullmaktRedisService = mockk()
    private val navnService: NavnService = mockk()
    private val fullmaktConsumer: FullmaktConsumer = mockk()

    private val fullmaktService = FullmaktService(fullmaktConsumer, navnService)

    private val ident = "123"

    private val fullmaktGiver1 = FullmaktGiver("111", "abc")


    @AfterEach
    fun cleanUp() {
        clearMocks(sakService, redisService, navnService, fullmaktConsumer)
    }

    @Test
    fun `henter sakstema for innlogget bruker hvis fullmaktsgiver ikke er satt`() = fullmaktApiTest { client ->
        coEvery {
            redisService.getCurrentFullmaktGiver(ident)
        } returns null

        coEvery {
            sakService.hentSakstemaer(any(), null)
        } returns sakstemaResponse(Sakstemakode.AAP)

        coEvery {
            sakService.hentSakstemaer(any(), fullmaktGiver1.ident)
        } returns sakstemaResponse(Sakstemakode.YRK)

        val response: List<ForenkletSakstema> = client.get("sakstemaer").body()

        response.first().kode shouldBe Sakstemakode.AAP
    }

    @Test
    fun `henter sakstema for fullmaktsgiver hvis den er satt for session`() = fullmaktApiTest { client ->

        coEvery {
            redisService.getCurrentFullmaktGiver(ident)
        } returns fullmaktGiver1

        coEvery {
            sakService.hentSakstemaer(any(), null)
        } returns sakstemaResponse(Sakstemakode.AAP)

        coEvery {
            sakService.hentSakstemaer(any(), fullmaktGiver1.ident)
        } returns sakstemaResponse(Sakstemakode.YRK)

        val response: List<ForenkletSakstema> = client.get("sakstemaer").body()

        response.first().kode shouldBe Sakstemakode.YRK
    }

    @Test
    fun `henter journalposter for innlogget bruker hvis fullmaktsgiver ikke er satt`() = fullmaktApiTest { client ->

        val navnForBruker = "Tilhører bruker"
        val navnForRepresentert = "Tilhører representert"

        coEvery {
            redisService.getCurrentFullmaktGiver(ident)
        } returns null

        coEvery {
            sakService.hentJournalposterForSakstema(any(), null, Sakstemakode.AAP)
        } returns journalpostResponse(navnForBruker)

        coEvery {
            sakService.hentJournalposterForSakstema(any(), fullmaktGiver1.ident, Sakstemakode.AAP)
        } returns journalpostResponse(navnForRepresentert)

        val responseForQuery: List<Sakstema> = client.get("journalposter?sakstemakode=AAP").body()
        val responseForPathParam: List<Sakstema> = client.get("journalposter/AAP").body()

        responseForQuery.first().navn shouldBe navnForBruker
        responseForPathParam.first().navn shouldBe navnForBruker
    }

    @Test
    fun `henter journalposter for fullmaktsgiver hvis den er satt`() = fullmaktApiTest { client ->
        val navnForBruker = "Tilhører bruker"
        val navnForRepresentert = "Tilhører representert"

        coEvery {
            redisService.getCurrentFullmaktGiver(ident)
        } returns fullmaktGiver1

        coEvery {
            sakService.hentJournalposterForSakstema(any(), null, Sakstemakode.AAP)
        } returns journalpostResponse(navnForBruker)

        coEvery {
            sakService.hentJournalposterForSakstema(any(), fullmaktGiver1.ident, Sakstemakode.AAP)
        } returns journalpostResponse(navnForRepresentert)

        val responseForQuery: List<Sakstema> = client.get("journalposter?sakstemakode=AAP").body()
        val responseForPathParam: List<Sakstema> = client.get("journalposter/AAP").body()

        responseForQuery.first().navn shouldBe navnForRepresentert
        responseForPathParam.first().navn shouldBe navnForRepresentert
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
                sakService = sakService,
                httpClient = testClient,
                corsAllowedOrigins = "*",
                corsAllowedSchemes = "*",
                sakerUrl = "N/A",
                fullmaktService = fullmaktService,
                fullmaktRedisService = redisService,
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

    private fun journalpostResponse(navn: String) = listOf(
        Sakstema(
            navn = navn,
            kode = Sakstemakode.AAP,
            journalposter = listOf(
                Journalpost(
                    tittel = "Tittel",
                    journalpostId = "123",
                    journalposttype = NOTAT,
                    avsender = null,
                    mottaker = null,
                    sisteEndret = ZonedDateTime.now(),
                    dokumenter = emptyList(),
                    harVedlegg = false,
                )
            )
        )
    )

}
