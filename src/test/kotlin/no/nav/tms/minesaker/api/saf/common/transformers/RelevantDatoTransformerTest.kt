package no.nav.tms.minesaker.api.saf.common.transformers

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import no.nav.tms.minesaker.api.saf.journalposter.v1.SafRelevantDato
import no.nav.tms.minesaker.api.saf.journalposter.v1.toInternal
import org.junit.jupiter.api.Test

internal class RelevantDatoTransformerTest {

    @Test
    fun `Skal konvertere dato som string til dato type med tidssone satt`() {
        val expectedYear = 2018
        val expectedMonth = 10
        val expectedDay = 11
        val expectedHour = 12
        val expectedMinutes = 40
        val expectedSeconds = 50
        val dateString = "$expectedYear-$expectedMonth-${expectedDay}T$expectedHour:$expectedMinutes:$expectedSeconds"

        val external = SafRelevantDato(dateString)

        val internal = external.toInternal()

        internal.shouldNotBeNull()
        internal.year shouldBe expectedYear
        internal.monthValue shouldBe expectedMonth
        internal.dayOfMonth shouldBe expectedDay
        internal.hour shouldBe expectedHour
        internal.minute shouldBe expectedMinutes
        internal.second shouldBe expectedSeconds
    }

}
