package no.nav.tms.minesaker.api.common

import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.Application
import io.ktor.server.auth.*
import io.ktor.server.testing.*
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.mockk
import no.nav.tms.minesaker.api.setup.DocumentNotFoundException
import no.nav.tms.minesaker.api.mineSakerApi
import no.nav.tms.minesaker.api.journalpost.SafConsumer
import no.nav.tms.minesaker.api.setup.TokendingsExchange
import no.nav.tms.minesaker.api.fullmakt.FullmaktService
import no.nav.tms.minesaker.api.fullmakt.FullmaktSessionStore
import no.nav.tms.minesaker.api.fullmakt.FullmaktTestSessionStore
import no.nav.tms.minesaker.api.journalpost.SafService
import no.nav.tms.token.support.idporten.sidecar.mock.LevelOfAssurance
import no.nav.tms.token.support.idporten.sidecar.mock.idPortenMock
import no.nav.tms.token.support.tokenx.validation.mock.tokenXMock
import org.junit.jupiter.api.Test

internal class ExceptionApiTest {

    private val testfnr = "1234"

    @Test
    fun dokumenter() = testApplication {

        val safconsumerMockk = mockk<SafConsumer>().also {
            coEvery {
                it.hentDokument(any(), any(), any(), any(), any())
            } returns Unit
        }

        val (fullmaktService, fullmaktValkeyService) = mockFullmakt()

        application {
            val sakserviceMock = createSakService(safConsumer = safconsumerMockk)

            mineSakerApi(
                safService = sakserviceMock,
                digiSosConsumer = mockk(),
                httpClient = mockk(),
                corsAllowedOrigins = "*",
                authConfig = { defaultAuthConfig() },
                fullmaktService = fullmaktService,
                fullmaktSessionStore = fullmaktValkeyService,
            )
        }

        client.get("/dokument/-/-").apply {
            status shouldBe HttpStatusCode.BadRequest
        }

        clearMocks(safconsumerMockk)
        coEvery { safconsumerMockk.hentDokument(any(), any(), any(), any(), any()) } throws DocumentNotFoundException("", "123", "456")

        client.get("/dokument/gghh11/hfajskk").apply {
            status shouldBe HttpStatusCode.NotFound
        }

    }

    fun createSakService(
        safConsumer: SafConsumer = mockk(),
    ) = SafService(
        safConsumer = safConsumer,
        tokendingsExchange = mockk<TokendingsExchange>().also {
            coEvery { it.safToken(any()) } returns "<dummytoken>"
            coEvery { it.digisosToken(any()) } returns "<dummytoken>"

        }
    )

    private fun Application.defaultAuthConfig() =
        authentication {
            idPortenMock {
                alwaysAuthenticated = true
                setAsDefault = true
                staticLevelOfAssurance = LevelOfAssurance.HIGH
                staticUserPid = testfnr
            }

            tokenXMock {
                alwaysAuthenticated = true
                setAsDefault = false
                staticLevelOfAssurance = no.nav.tms.token.support.tokenx.validation.mock.LevelOfAssurance.HIGH
                staticUserPid = testfnr
            }
        }

    private fun mockFullmakt(): Pair<FullmaktService, FullmaktSessionStore> {
        val fullmaktService: FullmaktService = mockk()
        val sessionStore = FullmaktTestSessionStore()

        return fullmaktService to sessionStore
    }

}
