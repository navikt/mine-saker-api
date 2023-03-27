package no.nav.personbruker.minesaker.api.common

import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.clearMocks
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.minesaker.api.common.exception.CommunicationException
import no.nav.personbruker.minesaker.api.common.exception.DocumentNotFoundException
import no.nav.personbruker.minesaker.api.common.exception.GraphQLResultException
import no.nav.personbruker.minesaker.api.common.exception.InvalidRequestException
import no.nav.personbruker.minesaker.api.config.Environment
import no.nav.personbruker.minesaker.api.config.mineSakerApi
import no.nav.tms.token.support.authentication.installer.mock.installMockedAuthenticators
import no.nav.tms.token.support.idporten.sidecar.mock.SecurityLevel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.Logger

internal class ExceptionApiTest {

    private val log = mockk<Logger>(relaxed = true)

    @BeforeEach
    fun clearMock() {
        clearMocks(log)
    }

    @Test
    fun `journalposter med queryparameter`() = testApplication {
        application {
            mineSakerApi(
                sakService = mockk(),
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
                            staticUserPid = "1234"

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


    }

    @Test
    fun `Skal haandtere DocumentNotFoundException`() {
        val exception = DocumentNotFoundException("Simulert feil")

        val errorCode = runBlocking {
            ExceptionResponseHandler.logExceptionAndDecideErrorResponseCode(log, exception)
        }

        errorCode shouldBe HttpStatusCode.NotFound
        coVerify(exactly = 1) { log.warn(any<String>(), any()) }
        confirmVerified(log)
    }

    @Test
    fun `Skal haandtere GraphQLResultException`() {
        val exception = GraphQLResultException("Simulert feil", emptyList(), emptyMap())

        val errorCode = runBlocking {
            ExceptionResponseHandler.logExceptionAndDecideErrorResponseCode(log, exception)
        }

        errorCode shouldBe HttpStatusCode.ServiceUnavailable
        coVerify(exactly = 1) { log.warn(any<String>(), any()) }
        confirmVerified(log)
    }

    @Test
    fun `Skal haandtere InvalidRequestException`() {
        val exception = InvalidRequestException("Simulert feil")

        val errorCode = runBlocking {
            ExceptionResponseHandler.logExceptionAndDecideErrorResponseCode(log, exception)
        }

        errorCode shouldBe HttpStatusCode.BadRequest
        coVerify(exactly = 1) { log.warn(any<String>(), any()) }
        confirmVerified(log)
    }

    @Test
    fun `Skal haandtere CommunicationException`() {
        val exception = CommunicationException("Simulert feil")

        val errorCode = runBlocking {
            ExceptionResponseHandler.logExceptionAndDecideErrorResponseCode(log, exception)
        }

        errorCode shouldBe HttpStatusCode.ServiceUnavailable
        coVerify(exactly = 1) { log.warn(any<String>(), any()) }
        confirmVerified(log)
    }

    @Test
    fun `Skal haandtere ukjente feil`() {
        val exception = SecurityException("Simulert feil")

        val errorCode = runBlocking {
            ExceptionResponseHandler.logExceptionAndDecideErrorResponseCode(log, exception)
        }

        errorCode shouldBe HttpStatusCode.InternalServerError
        coVerify(exactly = 1) { log.error(any<String>(), any()) }
        confirmVerified(log)
    }

}
