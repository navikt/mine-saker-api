package no.nav.tms.minesaker.api.saf.journalposter.transformers

import io.kotest.matchers.shouldBe
import no.nav.tms.minesaker.api.exception.TransformationException
import no.nav.tms.minesaker.api.saf.journalposter.v1.Journalposttype
import no.nav.tms.minesaker.api.saf.journalposter.v1.SafJournalposttype
import no.nav.tms.minesaker.api.saf.journalposter.v1.toInternal
import org.junit.jupiter.api.Test

class JournalposttypeTransformerTest {

    @Test
    fun `Skal transformere alle gyldige verdier, fra ekstern til intern verdi`() {
        SafJournalposttype.I.toInternal() shouldBe Journalposttype.INNGAAENDE
        SafJournalposttype.U.toInternal() shouldBe Journalposttype.UTGAAENDE
        SafJournalposttype.N.toInternal() shouldBe Journalposttype.NOTAT
    }

    @Test
    fun `Skal kaste feil ved ugyldig verdi`() {
        val result = runCatching {
            SafJournalposttype.__UNKNOWN_VALUE.toInternal()
        }
        result.isFailure shouldBe true
        val exception = result.exceptionOrNull() as TransformationException
        exception.type shouldBe TransformationException.ErrorType.UNKNOWN_VALUE
    }

}
