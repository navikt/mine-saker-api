package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter.Journalposttype.*
import no.nav.personbruker.minesaker.api.common.exception.UnknownValueException
import no.nav.personbruker.minesaker.api.saf.domain.Journalposttype
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be instance of`
import org.junit.jupiter.api.Test

internal class JournalposttypeTransformerTest {

    @Test
    fun `Skal transformere alle gyldige verdier, fra ekstern til intern verdi`() {
        I.toInternal() `should be equal to` Journalposttype.INNGAAENDE
        U.toInternal() `should be equal to` Journalposttype.UTGAAENDE
        N.toInternal() `should be equal to` Journalposttype.NOTAT
    }

    @Test
    fun `Skal kaste feil ved ugyldig verdi`() {
        val result = runCatching {
            __UNKNOWN_VALUE.toInternal()
        }
        result.isFailure `should be equal to` true
        result.exceptionOrNull() `should be instance of` UnknownValueException::class
        val mfe = result.exceptionOrNull() as UnknownValueException
        mfe.context["feltnavn"] `should be equal to` "journalposttype"
    }

}
