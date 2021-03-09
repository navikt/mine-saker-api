package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.personbruker.minesaker.api.common.exception.MissingFieldException
import no.nav.personbruker.minesaker.api.saf.domain.ID
import no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers.JournalpostObjectMother
import org.amshove.kluent.*
import org.junit.jupiter.api.Test

internal class JournalpostTransformerTest {

    private val dummyIdent = ID("123")

    @Test
    fun `Skal transformere til intern type`() {
        val external = JournalpostObjectMother.giveMeOneInngaaendeDokument()

        val internal = external.toInternal(dummyIdent)

        internal.shouldNotBeNull()
        internal.tittel.value `should be equal to` external.tittel
        internal.journalpostId.value `should be equal to` external.journalpostId

        internal.arkiverteDokumenter.shouldNotBeEmpty()
        internal.avsenderMottaker.shouldNotBeNull()
        internal.relevanteDatoer.shouldNotBeNull()
        internal.journalposttype.shouldNotBeNull()
    }

    @Test
    fun `Skal kaste feil hvis tittel ikke er satt`() {
        val external = JournalpostObjectMother.giveMeOneInngaaendeDokument(tittel = null)

        val result = runCatching {
            external.toInternal(dummyIdent)
        }

        result.isFailure `should be equal to` true
        result.exceptionOrNull() `should be instance of` MissingFieldException::class
        val exception = result.exceptionOrNull() as MissingFieldException
        exception.context["feltnavn"] `should be equal to` "tittel"
    }

    @Test
    fun `Skal kaste feil hvis avsenderMottaker ikke er satt`() {
        val external = JournalpostObjectMother.giveMeOneInngaaendeDokument(avsenderMottaker = null)

        val result = runCatching {
            external.toInternal(dummyIdent)
        }

        result.isFailure `should be equal to` true
        result.exceptionOrNull() `should be instance of` MissingFieldException::class
        val mfe = result.exceptionOrNull() as MissingFieldException
        mfe.context["feltnavn"] `should be equal to` "avsenderMottaker"
    }

    @Test
    fun `Skal kaste feil hvis journalposttype ikke er satt`() {
        val external = JournalpostObjectMother.giveMeOneInngaaendeDokument(journalposttype = null)

        val result = runCatching {
            external.toInternal(dummyIdent)
        }

        result.isFailure `should be equal to` true
        result.exceptionOrNull().`should not be null`()
        result.exceptionOrNull() `should be instance of` MissingFieldException::class
        val mfe = result.exceptionOrNull() as MissingFieldException
        mfe.context["feltnavn"] `should be equal to` "journalposttype"
    }

}
