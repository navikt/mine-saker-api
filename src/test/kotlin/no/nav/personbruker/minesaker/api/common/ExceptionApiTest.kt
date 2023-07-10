package no.nav.personbruker.minesaker.api.common

import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.Application
import io.ktor.server.testing.*
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.mockk
import no.nav.personbruker.minesaker.api.exception.CommunicationException
import no.nav.personbruker.minesaker.api.exception.DocumentNotFoundException
import no.nav.personbruker.minesaker.api.exception.GraphQLResultException
import no.nav.personbruker.minesaker.api.config.mineSakerApi
import no.nav.personbruker.minesaker.api.digisos.DigiSosConsumer
import no.nav.personbruker.minesaker.api.domain.ForenkletSakstema
import no.nav.personbruker.minesaker.api.domain.Sakstemakode
import no.nav.personbruker.minesaker.api.saf.SafConsumer
import no.nav.personbruker.minesaker.api.config.TokendingsExchange
import no.nav.personbruker.minesaker.api.saf.DokumentResponse
import no.nav.personbruker.minesaker.api.saf.fullmakt.FullmaktInterception
import no.nav.personbruker.minesaker.api.saf.fullmakt.FullmaktService
import no.nav.personbruker.minesaker.api.saf.fullmakt.FullmaktJwtService
import no.nav.personbruker.minesaker.api.sak.Kildetype
import no.nav.personbruker.minesaker.api.sak.SakService
import no.nav.personbruker.minesaker.api.sak.SakstemaResult
import no.nav.tms.token.support.idporten.sidecar.mock.LevelOfAssurance
import no.nav.tms.token.support.idporten.sidecar.mock.installIdPortenAuthMock
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

            val fullmaktService = mockk<FullmaktService>()
            val fullmaktJwtService = mockk<FullmaktJwtService>()
            val fullmaktInterception = FullmaktInterception(fullmaktJwtService)

            mineSakerApi(
                sakService = sakserviceMock,
                httpClient = mockk(),
                corsAllowedOrigins = "*",
                corsAllowedSchemes = "*",
                authConfig = { defaultAuthConfig() },
                sakerUrl = "http://minesaker.dev",
                fullmaktService = fullmaktService,
                fullmaktJwtService = fullmaktJwtService,
                fullmaktInterception = fullmaktInterception
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
                } throws GraphQLResultException("Ingen data i resultatet fra SAF.", listOf(), mapOf())
            })

            val fullmaktService = mockk<FullmaktService>()
            val fullmaktJwtService = mockk<FullmaktJwtService>()
            val fullmaktInterception = FullmaktInterception(fullmaktJwtService)

            mineSakerApi(
                sakService = sakserviceMock,
                httpClient = mockk(),
                corsAllowedOrigins = "*",
                corsAllowedSchemes = "*",
                authConfig = { defaultAuthConfig() },
                sakerUrl = "http://minesaker.dev",
                fullmaktService = fullmaktService,
                fullmaktJwtService = fullmaktJwtService,
                fullmaktInterception = fullmaktInterception
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
            coEvery { it.hentSakstemaer(any(), any()) } returns SakstemaResult(errors = listOf(Kildetype.SAF))
        }

        val fullmaktService = mockk<FullmaktService>()
        val fullmaktJwtService = mockk<FullmaktJwtService>()
        val fullmaktInterception = FullmaktInterception(fullmaktJwtService)

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
                fullmaktJwtService = fullmaktJwtService,
                fullmaktInterception = fullmaktInterception
            )
        }

        client.get("/sakstemaer").apply {
            status shouldBe HttpStatusCode.OK
        }
        coEvery { digiSosConsumerMockk.hentSakstemaer(any()) } returns SakstemaResult(errors = listOf(Kildetype.DIGISOS))

        client.get("/sakstemaer").apply {
            status shouldBe HttpStatusCode.ServiceUnavailable
        }
    }

    @Test
    fun dokumenter() = testApplication {

        val safconsumerMockk = mockk<SafConsumer>().also {
            coEvery {
                it.hentDokument(any(), any(), any())
            } returns DokumentResponse(ByteArray(10), ContentType.Application.Pdf)
        }

        val fullmaktService = mockk<FullmaktService>()
        val fullmaktJwtService = mockk<FullmaktJwtService>()
        val fullmaktInterception = FullmaktInterception(fullmaktJwtService)

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
                fullmaktJwtService = fullmaktJwtService,
                fullmaktInterception = fullmaktInterception
            )
        }
        client.get("/dokument/gghh11/hfajskk").apply {
            status shouldBe HttpStatusCode.OK
        }
        client.get("/dokument/-/-").apply {
            status shouldBe HttpStatusCode.BadRequest
        }

        clearMocks(safconsumerMockk)
        coEvery { safconsumerMockk.hentDokument(any(), any(), any()) } throws DocumentNotFoundException("")

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
        installIdPortenAuthMock {
            alwaysAuthenticated = true
            setAsDefault = true
            staticLevelOfAssurance = LevelOfAssurance.LEVEL_4
            staticUserPid = testfnr

        }

}
