package no.nav.personbruker.minesaker.api.common

import io.ktor.http.*
import io.mockk.clearMocks
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.minesaker.api.common.exception.CommunicationException
import no.nav.personbruker.minesaker.api.common.exception.DocumentNotFoundException
import no.nav.personbruker.minesaker.api.common.exception.GraphQLResultException
import no.nav.personbruker.minesaker.api.common.exception.InvalidRequestException
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.Logger

internal class ExceptionResponseHandlerTest {

    private val log = mockk<Logger>(relaxed = true)

    @BeforeEach
    fun clearMock() {
        clearMocks(log)
    }

    @Test
    fun `Skal haandtere DocumentNotFoundException`() {
        val exception = DocumentNotFoundException("Simulert feil")

        val errorCode = runBlocking {
            ExceptionResponseHandler.logExceptionAndDecideErrorResponseCode(log, exception)
        }

        errorCode `should be equal to` HttpStatusCode.NotFound
        coVerify(exactly = 1) { log.warn(any<String>(), any()) }
        confirmVerified(log)
    }

    @Test
    fun `Skal haandtere GraphQLResultException`() {
        val exception = GraphQLResultException("Simulert feil", emptyList(), emptyMap())

        val errorCode = runBlocking {
            ExceptionResponseHandler.logExceptionAndDecideErrorResponseCode(log, exception)
        }

        errorCode `should be equal to` HttpStatusCode.ServiceUnavailable
        coVerify(exactly = 1) { log.warn(any<String>(), any()) }
        confirmVerified(log)
    }

    @Test
    fun `Skal haandtere InvalidRequestException`() {
        val exception = InvalidRequestException("Simulert feil")

        val errorCode = runBlocking {
            ExceptionResponseHandler.logExceptionAndDecideErrorResponseCode(log, exception)
        }

        errorCode `should be equal to` HttpStatusCode.BadRequest
        coVerify(exactly = 1) { log.warn(any<String>(), any()) }
        confirmVerified(log)
    }

    @Test
    fun `Skal haandtere CommunicationException`() {
        val exception = CommunicationException("Simulert feil")

        val errorCode = runBlocking {
            ExceptionResponseHandler.logExceptionAndDecideErrorResponseCode(log, exception)
        }

        errorCode `should be equal to` HttpStatusCode.ServiceUnavailable
        coVerify(exactly = 1) { log.warn(any<String>(), any()) }
        confirmVerified(log)
    }

    @Test
    fun `Skal haandtere ukjente feil`() {
        val exception = SecurityException("Simulert feil")

        val errorCode = runBlocking {
            ExceptionResponseHandler.logExceptionAndDecideErrorResponseCode(log, exception)
        }

        errorCode `should be equal to` HttpStatusCode.InternalServerError
        coVerify(exactly = 1) { log.error(any<String>(), any()) }
        confirmVerified(log)
    }

}
