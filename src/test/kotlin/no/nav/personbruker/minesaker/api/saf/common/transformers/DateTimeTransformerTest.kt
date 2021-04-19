package no.nav.personbruker.minesaker.api.saf.common.transformers

import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should not be null`
import org.junit.jupiter.api.Test

internal class DateTimeTransformerTest {

    @Test
    fun `Skal konvertere dato som string til dato type med tidssone satt`() {
        val expectedYear = 2018
        val expectedMonth = 10
        val expectedDay = 11
        val expectedHour = 12
        val expectedMinutes = 40
        val expectedSeconds = 50
        val external = "$expectedYear-$expectedMonth-${expectedDay}T$expectedHour:$expectedMinutes:$expectedSeconds"

        val internal = external.toInternal()

        internal.`should not be null`()
        internal.year `should be equal to` expectedYear
        internal.monthValue `should be equal to` expectedMonth
        internal.dayOfMonth `should be equal to` expectedDay
        internal.hour `should be equal to` expectedHour
        internal.minute `should be equal to` expectedMinutes
        internal.second `should be equal to` expectedSeconds
    }

}
