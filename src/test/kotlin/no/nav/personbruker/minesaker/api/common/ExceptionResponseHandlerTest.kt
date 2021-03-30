package no.nav.personbruker.minesaker.api.common

import io.ktor.http.*
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.minesaker.api.common.exception.*
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import org.slf4j.Logger

internal class ExceptionResponseHandlerTest {

    @Test
    fun `Skal haandtere DocumentNotFoundException`() {
        val log = mockk<Logger>(relaxed = true)
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
        val log = mockk<Logger>(relaxed = true)
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
        val log = mockk<Logger>(relaxed = true)
        val exception = InvalidRequestException("Simulert feil")

        val errorCode = runBlocking {
            ExceptionResponseHandler.logExceptionAndDecideErrorResponseCode(log, exception)
        }

        errorCode `should be equal to` HttpStatusCode.BadRequest
        coVerify(exactly = 1) { log.warn(any<String>(), any()) }
        confirmVerified(log)
    }

    @Test
    fun `Skal haandtere MissingFieldException`() {
        val log = mockk<Logger>(relaxed = true)
        val exception = MissingFieldException("Simulert feil")

        val errorCode = runBlocking {
            ExceptionResponseHandler.logExceptionAndDecideErrorResponseCode(log, exception)
        }

        errorCode `should be equal to` HttpStatusCode.ServiceUnavailable
        coVerify(exactly = 1) { log.warn(any<String>(), any()) }
        confirmVerified(log)
    }

    @Test
    fun `Skal haandtere CommunicationException`() {
        val log = mockk<Logger>(relaxed = true)
        val exception = CommunicationException("Simulert feil")

        val errorCode = runBlocking {
            ExceptionResponseHandler.logExceptionAndDecideErrorResponseCode(log, exception)
        }

        errorCode `should be equal to` HttpStatusCode.ServiceUnavailable
        coVerify(exactly = 1) { log.warn(any<String>(), any()) }
        confirmVerified(log)
    }

    @Test
    fun `Skal haandtere UgyldigVerdiException`() {
        val log = mockk<Logger>(relaxed = true)
        val exception = UgyldigVerdiException("Simulert feil")

        val errorCode = runBlocking {
            ExceptionResponseHandler.logExceptionAndDecideErrorResponseCode(log, exception)
        }

        errorCode `should be equal to` HttpStatusCode.InternalServerError
        coVerify(exactly = 1) { log.warn(any<String>(), any()) }
        confirmVerified(log)
    }

    @Test
    fun `Skal haandtere UnknownValueException`() {
        val log = mockk<Logger>(relaxed = true)
        val exception = UnknownValueException("Simulert feil")

        val errorCode = runBlocking {
            ExceptionResponseHandler.logExceptionAndDecideErrorResponseCode(log, exception)
        }

        errorCode `should be equal to` HttpStatusCode.InternalServerError
        coVerify(exactly = 1) { log.warn(any<String>(), any()) }
        confirmVerified(log)
    }

    @Test
    fun `Skal haandtere ukjente feil`() {
        val log = mockk<Logger>(relaxed = true)
        val exception = SecurityException("Simulert feil")

        val errorCode = runBlocking {
            ExceptionResponseHandler.logExceptionAndDecideErrorResponseCode(log, exception)
        }

        errorCode `should be equal to` HttpStatusCode.InternalServerError
        coVerify(exactly = 1) { log.error(any<String>(), any()) }
        confirmVerified(log)
    }

}
