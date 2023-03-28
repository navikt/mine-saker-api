package no.nav.personbruker.minesaker.api.common

import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.mockk
import no.nav.personbruker.minesaker.api.common.exception.CommunicationException
import no.nav.personbruker.minesaker.api.common.exception.DocumentNotFoundException
import no.nav.personbruker.minesaker.api.common.exception.GraphQLResultException
import no.nav.personbruker.minesaker.api.config.mineSakerApi
import no.nav.personbruker.minesaker.api.digisos.DigiSosConsumer
import no.nav.personbruker.minesaker.api.digisos.DigiSosTokendings
import no.nav.personbruker.minesaker.api.domain.AuthenticatedUser
import no.nav.personbruker.minesaker.api.saf.SafConsumer
import no.nav.personbruker.minesaker.api.saf.SafTokendings
import no.nav.personbruker.minesaker.api.sak.Kildetype
import no.nav.personbruker.minesaker.api.sak.SakService
import no.nav.personbruker.minesaker.api.sak.SakstemaResult
import no.nav.tms.token.support.authentication.installer.mock.installMockedAuthenticators
import no.nav.tms.token.support.idporten.sidecar.mock.SecurityLevel
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser
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
            mineSakerApi(
                sakService = sakserviceMock,
                httpClient = mockk(),
                corsAllowedOrigins = "*",
                corsAllowedSchemes = "*",
                rootPath = "mine-saker-api",
                authConfig = {
                    installMockedAuthenticators {
                        installIdPortenAuthMock {
                            alwaysAuthenticated = true
                            setAsDefault = true
                            staticSecurityLevel = SecurityLevel.LEVEL_4
                            staticUserPid = testfnr

                        }
                    }
                },
            )
        }

        client.get("/mine-saker-api/journalposter").apply {
            status shouldBe HttpStatusCode.BadRequest
            bodyAsText() shouldBe "Parameter sakstemakode mangler"
        }
        client.get("/mine-saker-api/journalposter?sakstemakode=UGLYDIG").apply {
            status shouldBe HttpStatusCode.BadRequest
            bodyAsText() shouldBe "Ugyldig verdi for sakstemakode"
        }
        client.get("/mine-saker-api/journalposter?sakstemakode=AAP").apply {
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
            mineSakerApi(
                sakService = sakserviceMock,
                httpClient = mockk(),
                corsAllowedOrigins = "*",
                corsAllowedSchemes = "*",
                rootPath = "mine-saker-api",
                authConfig = {
                    installMockedAuthenticators {
                        installIdPortenAuthMock {
                            alwaysAuthenticated = true
                            setAsDefault = true
                            staticSecurityLevel = SecurityLevel.LEVEL_4
                            staticUserPid = testfnr

                        }
                    }
                },
            )
        }

        client.get("/mine-saker-api/journalposter/UGLYDIG").apply {
            status shouldBe HttpStatusCode.BadRequest
            bodyAsText() shouldBe "Ugyldig verdi for sakstemakode"
        }
        client.get("/mine-saker-api/journalposter/AAP").apply {
            status shouldBe HttpStatusCode.InternalServerError
        }

    }

    @Test
    fun sakstema() = testApplication {
        val safConsumerMock = mockk<SafConsumer>().also {
            coEvery { it.hentSakstemaer(any(), any()) } returns SakstemaResult(errors = listOf(Kildetype.SAF))
        }
        val digiSosConsumerMockk = mockk<DigiSosConsumer>().also {
            coEvery { it.hentSakstemaer(any()) } returns SakstemaResult(results = listOf(mockk()))
        }
        application {
            val sakserviceMock = createSakService(safConsumer = safConsumerMock, digiSosConsumer = digiSosConsumerMockk)
            mineSakerApi(
                sakService = sakserviceMock,
                httpClient = mockk(),
                corsAllowedOrigins = "*",
                corsAllowedSchemes = "*",
                rootPath = "mine-saker-api",
                authConfig = {
                    installMockedAuthenticators {
                        installIdPortenAuthMock {
                            alwaysAuthenticated = true
                            setAsDefault = true
                            staticSecurityLevel = SecurityLevel.LEVEL_4
                            staticUserPid = testfnr

                        }
                    }
                },
            )
        }

        client.get("/mine-saker-api/sakstemaer").apply {
            status shouldBe HttpStatusCode.OK
        }
        clearMocks(digiSosConsumerMockk)
        coEvery { digiSosConsumerMockk.hentSakstemaer(any()) } returns SakstemaResult(errors = listOf(Kildetype.DIGISOS))
        client.get("/mine-saker-api/sakstemaer").apply {
            status shouldBe HttpStatusCode.ServiceUnavailable
        }
    }

    @Test
    fun dokumenter() = testApplication {
        val safconsumerMockk = mockk<SafConsumer>().also {
            coEvery {
                it.hentDokument(any(), any(), any())
            } returns ByteArray(10)
        }

        application {
            val sakserviceMock = createSakService(safConsumer = safconsumerMockk)

            mineSakerApi(
                sakService = sakserviceMock,
                httpClient = mockk(),
                corsAllowedOrigins = "*",
                corsAllowedSchemes = "*",
                rootPath = "mine-saker-api",
                authConfig = {
                    installMockedAuthenticators {
                        installIdPortenAuthMock {
                            alwaysAuthenticated = true
                            setAsDefault = true
                            staticSecurityLevel = SecurityLevel.LEVEL_4
                            staticUserPid = testfnr

                        }
                    }
                },
            )
        }
        client.get("/mine-saker-api/dokument/gghh11/hfajskk").apply {
            status shouldBe HttpStatusCode.OK
        }
        client.get("/mine-saker-api/dokument/-/-").apply {
            status shouldBe HttpStatusCode.BadRequest
        }

        clearMocks(safconsumerMockk)
        coEvery { safconsumerMockk.hentDokument(any(), any(), any()) } throws DocumentNotFoundException("")

        client.get("/mine-saker-api/dokument/gghh11/hfajskk").apply {
            status shouldBe HttpStatusCode.NotFound
        }

    }

    fun createSakService(
        safConsumer: SafConsumer = mockk(),
        digiSosConsumer: DigiSosConsumer = mockk()
    ) = SakService(
        safConsumer = safConsumer,
        safTokendings = mockk<SafTokendings>().also {
            coEvery { it.exchangeToken(any<IdportenUser>()) } returns "<dummytoken>"
            coEvery { it.exchangeToken(any<AuthenticatedUser>()) } returns "<dummytoken>"

        },
        digiSosConsumer = digiSosConsumer,
        digiSosTokendings = mockk<DigiSosTokendings>().also {
            coEvery { it.exchangeToken(any()) } returns "<dummytoken>"
        }
    )

}
