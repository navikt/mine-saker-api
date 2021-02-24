package no.nav.personbruker.minesaker.api.saf.domain

import org.amshove.kluent.`should contain`
import org.amshove.kluent.`should not contain`
import org.junit.jupiter.api.Test

internal class AvsenderMottakerTest {

    @Test
    fun `Det skal ikke vaere mulig aa skrive ut id-en vha toString-metoden`() {
        val actualId = "123456"
        val expectedType = AvsenderMottakerType.PERSON
        val avsender = AvsenderMottaker(actualId, expectedType)
        avsender.toString() `should not contain` actualId
        avsender.toString() `should contain` "id='***'"
        avsender.toString() `should contain` expectedType.toString()
    }

}
