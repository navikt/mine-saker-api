package no.nav.personbruker.minesaker.api.sak

import io.ktor.http.*
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

internal class SakstemaResultTest {

    @Test
    fun `Skal returnere http-kode ok hvis alle kilder svarer`() {
        val result = SakstemaResultObjectMother.createSafResults()

        result.determineHttpCode() `should be equal to` HttpStatusCode.OK
    }

    @Test
    fun `Skal returnere http-kode partial result hvis en kilde ikke svarer`() {
        val result = SakstemaResultObjectMother.createResultWithOneError()

        result.determineHttpCode() `should be equal to` HttpStatusCode.PartialContent
    }

    @Test
    fun `Skal returnere http-kode service unavailable hvis ingen kilder svarer`() {
        val result = SakstemaResultObjectMother.createResultWithTwoErrors()

        result.determineHttpCode() `should be equal to` HttpStatusCode.ServiceUnavailable
    }
    
}
