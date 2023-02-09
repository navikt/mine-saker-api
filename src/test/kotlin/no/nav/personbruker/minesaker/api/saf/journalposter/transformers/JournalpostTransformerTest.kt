package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.personbruker.minesaker.api.common.exception.TransformationException

import no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers.JournalpostObjectMother
import org.junit.jupiter.api.Test

internal class JournalpostTransformerTest {

    private val dummyIdent = "123"

    @Test
    fun `Skal transformere til intern type`() {
        val external = JournalpostObjectMother.giveMeOneInngaaendeDokument()

        val internal = external.toInternal(dummyIdent)

        internal.shouldNotBeNull()
        internal.tittel shouldBe  external.tittel
        internal.journalpostId shouldBe external.journalpostId

        internal.dokumenter.shouldNotBeEmpty()
        internal.avsender.shouldNotBeNull()
        internal.mottaker.shouldNotBeNull()
        internal.sisteEndret.shouldNotBeNull()
        internal.journalposttype.shouldNotBeNull()
    }

    @Test
    fun `Skal takle at tittel ikke er tilgjengelig i SAF, return dummy tittel til sluttbruker`() {
        val external = JournalpostObjectMother.giveMeOneInngaaendeDokument(tittel = null)

        val result = external.toInternal(dummyIdent)

        result.tittel shouldBe "Uten tittel"
    }

    @Test
    fun `Skal kaste feil hvis dokumentlisten er null`() {
        val external = JournalpostObjectMother.giveMeOneInngaaendeDokument(dokumenter = null)

        val result = runCatching {
            external.toInternal(dummyIdent)
        }

        result.isFailure shouldBe true
        result.exceptionOrNull().shouldBeInstanceOf<TransformationException>()
        val exception = result.exceptionOrNull() as TransformationException
        exception.context[TransformationException.feltnavnKey] shouldBe "dokumenter"
    }

}
