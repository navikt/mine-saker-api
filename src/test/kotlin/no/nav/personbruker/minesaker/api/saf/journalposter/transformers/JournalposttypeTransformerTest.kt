package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter.Journalposttype.*
import no.nav.personbruker.minesaker.api.common.exception.MissingFieldException
import no.nav.personbruker.minesaker.api.common.exception.UnknownValueException
import no.nav.personbruker.minesaker.api.saf.domain.Journalposttype
import no.nav.personbruker.minesaker.api.saf.journalposter.transformers.JournalposttypeTransformer
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be instance of`
import org.amshove.kluent.`should not be null`
import org.junit.jupiter.api.Test

internal class JournalposttypeTransformerTest {

    @Test
    fun `Skal transformere alle gyldige verdier, fra ekstern til intern verdi`() {
        JournalposttypeTransformer.toInternal(I) `should be equal to` Journalposttype.INNGAAENDE
        JournalposttypeTransformer.toInternal(U) `should be equal to` Journalposttype.UTGAAENDE
        JournalposttypeTransformer.toInternal(N) `should be equal to` Journalposttype.NOTAT
    }

    @Test
    fun `Skal kaste feil ved ugyldig verdi`() {
        val result = runCatching {
            JournalposttypeTransformer.toInternal(__UNKNOWN_VALUE)
        }
        result.isFailure `should be equal to` true
        result.exceptionOrNull() `should be instance of` UnknownValueException::class
        val mfe = result.exceptionOrNull() as UnknownValueException
        mfe.context["feltnavn"] `should be equal to` "journalposttype"
    }

    @Test
    fun `Skal kaste feil hvis input-en til metoden er null`() {
        val result = runCatching {
            JournalposttypeTransformer.toInternal(null)
        }
        result.isFailure `should be equal to` true
        result.exceptionOrNull().`should not be null`()
        result.exceptionOrNull() `should be instance of` MissingFieldException::class
        val mfe = result.exceptionOrNull() as MissingFieldException
        mfe.context["feltnavn"] `should be equal to` "journalposttype"
    }

}
