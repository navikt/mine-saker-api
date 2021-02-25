package no.nav.personbruker.minesaker.api.saf.transformers

import no.nav.personbruker.minesaker.api.common.exception.MissingFieldException
import no.nav.personbruker.minesaker.api.saf.domain.JournalpostObjectMother
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be instance of`
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test

internal class JournalpostTransformerTest {

    @Test
    fun `Skal transformere til interne typer`() {
        val external = JournalpostObjectMother.giveMeOneInngaaendeDokument()

        val internal = JournalpostTransformer.toInternal(external)

        internal.shouldNotBeNull()
        internal.tittel `should be equal to` external.tittel
        internal.journalpostId `should be equal to` external.journalpostId

        internal.arkiverteDokumenter.shouldNotBeEmpty()
        internal.avsenderMottaker.shouldNotBeNull()
        internal.datoRegistert.shouldNotBeNull()
        internal.journalposttype.shouldNotBeNull()
    }

    @Test
    fun `Skal kaste feil hvis tittel ikke er satt`() {
        val external = JournalpostObjectMother.giveMeUtenTittel()

        val result = runCatching {
            JournalpostTransformer.toInternal(external)
        }

        result.isFailure `should be equal to` true
        result.exceptionOrNull() `should be instance of` MissingFieldException::class
        val exception = result.exceptionOrNull() as MissingFieldException
        exception.context["feltnavn"] `should be equal to` "tittel"
    }

}
