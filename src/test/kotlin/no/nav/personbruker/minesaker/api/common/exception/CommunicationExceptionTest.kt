package no.nav.personbruker.minesaker.api.common.exception

import io.ktor.http.*
import org.amshove.kluent.`should contain`
import org.junit.jupiter.api.Test

internal class CommunicationExceptionTest {

    @Test
    fun `ToString-metoden skal inneholde exception-spesifikke verdier`() {
        val expectedMessage = "Dummy message"
        val expectedCode = HttpStatusCode.Accepted

        val ce = CommunicationException(expectedMessage, expectedCode)

        ce.toString() `should contain` expectedMessage
        ce.toString() `should contain` expectedCode.toString()
    }

}
