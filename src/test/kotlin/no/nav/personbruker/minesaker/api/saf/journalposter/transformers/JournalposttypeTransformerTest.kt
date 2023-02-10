package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import io.kotest.matchers.shouldBe
import no.nav.personbruker.minesaker.api.common.exception.TransformationException
import no.nav.personbruker.minesaker.api.domain.Journalposttype
import org.junit.jupiter.api.Test

internal class JournalposttypeTransformerTest {

    @Test
    fun `Skal transformere alle gyldige verdier, fra ekstern til intern verdi`() {
        GraphQLJournalposttype.I.toInternal() shouldBe Journalposttype.INNGAAENDE
        GraphQLJournalposttype.U.toInternal() shouldBe Journalposttype.UTGAAENDE
        GraphQLJournalposttype.N.toInternal() shouldBe Journalposttype.NOTAT
    }

    @Test
    fun `Skal kaste feil ved ugyldig verdi`() {
        val result = runCatching {
            GraphQLJournalposttype.__UNKNOWN_VALUE.toInternal()
        }
        result.isFailure shouldBe true
        val exception = result.exceptionOrNull() as TransformationException
        exception.type shouldBe TransformationException.ErrorType.UNKNOWN_VALUE
    }

}
