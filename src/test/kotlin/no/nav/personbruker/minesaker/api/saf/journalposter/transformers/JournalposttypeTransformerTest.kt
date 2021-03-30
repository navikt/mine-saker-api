package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter.Journalposttype.*
import no.nav.personbruker.minesaker.api.common.exception.TransformationException
import no.nav.personbruker.minesaker.api.saf.domain.Journalposttype
import org.amshove.kluent.`should be equal to`
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
        val exception = result.exceptionOrNull() as TransformationException
        exception.type `should be equal to` TransformationException.ErrorType.UNKNOWN_VALUE
    }

}
