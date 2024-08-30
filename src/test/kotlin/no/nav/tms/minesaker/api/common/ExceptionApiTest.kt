package no.nav.tms.minesaker.api.common

import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.Application
import io.ktor.server.auth.*
import io.ktor.server.testing.*
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.mockk
import no.nav.tms.minesaker.api.config.SubstantialAuth
import no.nav.tms.minesaker.api.exception.CommunicationException
import no.nav.tms.minesaker.api.exception.DocumentNotFoundException
import no.nav.tms.minesaker.api.exception.SafResultException
import no.nav.tms.minesaker.api.config.mineSakerApi
import no.nav.tms.minesaker.api.digisos.DigiSosConsumer
import no.nav.tms.minesaker.api.saf.sakstemaer.Sakstemakode
import no.nav.tms.minesaker.api.saf.SafConsumer
import no.nav.tms.minesaker.api.config.TokendingsExchange
import no.nav.tms.minesaker.api.saf.fullmakt.FullmaktService
import no.nav.tms.minesaker.api.saf.fullmakt.FullmaktSessionStore
import no.nav.tms.minesaker.api.saf.fullmakt.FullmaktTestSessionStore
import no.nav.tms.minesaker.api.saf.sakstemaer.ForenkletSakstema
import no.nav.tms.minesaker.api.saf.sakstemaer.Kildetype
import no.nav.tms.minesaker.api.sak.SakService
import no.nav.tms.minesaker.api.saf.sakstemaer.SakstemaResult
import no.nav.tms.token.support.idporten.sidecar.mock.LevelOfAssurance
import no.nav.tms.token.support.idporten.sidecar.mock.idPortenMock
import org.junit.jupiter.api.Test

internal class ExceptionApiTest {

    private val testfnr = "1234"

    @Test
    fun `journalposter med queryparameter`() = testApplication {
        application {
            val sakserviceMock = createSakService(safConsumer = mockk<SafConsumer>().also {
                coEvery {
                    it.hentJournalposter(any(), any(), any())
                } throws CommunicationException("Fikk http-status [500] fra SAF.")
            })

            val (fullmaktService, fullmaktRedisService) = mockFullmakt()

            mineSakerApi(
                sakService = sakserviceMock,
                httpClient = mockk(),
                corsAllowedOrigins = "*",
                corsAllowedSchemes = "*",
                authConfig = { defaultAuthConfig() },
                sakerUrl = "http://minesaker.dev",
                fullmaktService = fullmaktService,
                fullmaktSessionStore = fullmaktRedisService,
            )
        }

        client.get("/journalposter").apply {
            status shouldBe HttpStatusCode.BadRequest
            bodyAsText() shouldBe "Parameter sakstemakode mangler"
        }
        client.get("/journalposter?sakstemakode=UGLYDIG").apply {
            status shouldBe HttpStatusCode.BadRequest
            bodyAsText() shouldBe "Ugyldig verdi for sakstemakode"
        }
        client.get("/journalposter?sakstemakode=AAP").apply {
            status shouldBe HttpStatusCode.ServiceUnavailable
        }

    }

    @Test
    fun `journalposter med pathparameter`() = testApplication {
        application {
            val sakserviceMock = createSakService(safConsumer = mockk<SafConsumer>().also {
                coEvery {
                    it.hentJournalposter(any(), any(), any())
                } throws SafResultException("Ingen data i resultatet fra SAF.", listOf(), mapOf())
            })

            val (fullmaktService, fullmaktRedisService) = mockFullmakt()

            mineSakerApi(
                sakService = sakserviceMock,
                httpClient = mockk(),
                corsAllowedOrigins = "*",
                corsAllowedSchemes = "*",
                authConfig = { defaultAuthConfig() },
                sakerUrl = "http://minesaker.dev",
                fullmaktService = fullmaktService,
                fullmaktSessionStore = fullmaktRedisService,
            )
        }

        client.get("/journalposter/UGLYDIG").apply {
            status shouldBe HttpStatusCode.BadRequest
            bodyAsText() shouldBe "Ugyldig verdi for sakstemakode"
        }
        client.get("/journalposter/AAP").apply {
            status shouldBe HttpStatusCode.InternalServerError
        }

    }

    @Test
    fun sakstema() = testApplication {
        val safConsumerMock = mockk<SafConsumer>().also {
            coEvery { it.hentSakstemaer(any(), any()) } returns SakstemaResult.withErrors(listOf(Kildetype.SAF))
        }

        val (fullmaktService, fullmaktRedisService) = mockFullmakt()

        val digiSosConsumerMockk = mockk<DigiSosConsumer>().also {
            coEvery { it.hentSakstemaer(any()) } returns SakstemaResult(
                results = listOf(
                    ForenkletSakstema(
                        navn = "Navnese",
                        kode = Sakstemakode.AAP,
                        sistEndret = null,
                        detaljvisningUrl = "https://detaljer.test"
                    )
                )
            )
        }
        application {
            val sakserviceMock = createSakService(safConsumer = safConsumerMock, digiSosConsumer = digiSosConsumerMockk)
            mineSakerApi(
                sakService = sakserviceMock,
                httpClient = mockk(),
                corsAllowedOrigins = "*",
                corsAllowedSchemes = "*",
                authConfig = { defaultAuthConfig() },
                sakerUrl = "http://minesaker.dev",
                fullmaktService = fullmaktService,
                fullmaktSessionStore = fullmaktRedisService,
            )
        }

        client.get("/sakstemaer").apply {
            status shouldBe HttpStatusCode.OK
        }
        coEvery { digiSosConsumerMockk.hentSakstemaer(any()) } returns SakstemaResult.withErrors(listOf(Kildetype.DIGISOS))

        client.get("/sakstemaer").apply {
            status shouldBe HttpStatusCode.ServiceUnavailable
        }
    }

    @Test
    fun dokumenter() = testApplication {

        val safconsumerMockk = mockk<SafConsumer>().also {
            coEvery {
                it.hentDokument(any(), any(), any(), any())
            } returns Unit
        }

        val (fullmaktService, fullmaktRedisService) = mockFullmakt()

        application {
            val sakserviceMock = createSakService(safConsumer = safconsumerMockk)

            mineSakerApi(
                sakService = sakserviceMock,
                httpClient = mockk(),
                corsAllowedOrigins = "*",
                corsAllowedSchemes = "*",
                authConfig = { defaultAuthConfig() },
                sakerUrl = "http://minesaker.dev",
                fullmaktService = fullmaktService,
                fullmaktSessionStore = fullmaktRedisService,
            )
        }

        client.get("/dokument/-/-").apply {
            status shouldBe HttpStatusCode.BadRequest
        }

        clearMocks(safconsumerMockk)
        coEvery { safconsumerMockk.hentDokument(any(), any(), any(), any()) } throws DocumentNotFoundException("", "123", "456")

        client.get("/dokument/gghh11/hfajskk").apply {
            status shouldBe HttpStatusCode.NotFound
        }

    }

    fun createSakService(
        safConsumer: SafConsumer = mockk(),
        digiSosConsumer: DigiSosConsumer = mockk()
    ) = SakService(
        safConsumer = safConsumer,
        tokendingsExchange = mockk<TokendingsExchange>().also {
            coEvery { it.safToken(any()) } returns "<dummytoken>"
            coEvery { it.digisosToken(any()) } returns "<dummytoken>"

        },
        digiSosConsumer = digiSosConsumer
    )

    private fun Application.defaultAuthConfig() =
        authentication {
            idPortenMock {
                alwaysAuthenticated = true
                setAsDefault = true
                staticLevelOfAssurance = LevelOfAssurance.HIGH
                staticUserPid = testfnr
            }

            idPortenMock {
                authenticatorName = SubstantialAuth
                alwaysAuthenticated = true
                setAsDefault = false
                staticLevelOfAssurance = LevelOfAssurance.SUBSTANTIAL
                staticUserPid = testfnr
            }
        }

    private fun mockFullmakt(): Pair<FullmaktService, FullmaktSessionStore> {
        val fullmaktService: FullmaktService = mockk()
        val sessionStore = FullmaktTestSessionStore()

        return fullmaktService to sessionStore
    }

}
