package no.nav.personbruker.minesaker.api.saf.domain

import org.amshove.kluent.`should contain`
import org.amshove.kluent.`should not contain`
import org.junit.jupiter.api.Test

internal class IDTest {

    @Test
    fun `ID sin toString-metode skal ikke inneholde selve id-en`() {
        val actualId = "123"

        val id = ID(actualId)

        id.toString() `should not contain` actualId
        id.toString() `should contain` "***"
    }
}