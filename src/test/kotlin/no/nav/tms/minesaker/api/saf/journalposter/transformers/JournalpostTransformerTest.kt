package no.nav.tms.minesaker.api.saf.journalposter.transformers

import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.tms.minesaker.api.exception.TransformationException

import no.nav.tms.minesaker.api.saf.journalposter.JournalpostTestData
import no.nav.tms.minesaker.api.saf.journalposter.v1.toInternal
import org.junit.jupiter.api.Test

internal class JournalpostTransformerTest {

    private val dummyIdent = "123"

    @Test
    fun `Skal transformere til intern type`() {
        val external = JournalpostTestData.inngaaendeDokument()

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
        val external = JournalpostTestData.inngaaendeDokument(tittel = null)

        val result = external.toInternal(dummyIdent)

        result.tittel shouldBe "Uten tittel"
    }

    @Test
    fun `Skal kaste feil hvis dokumentlisten er null`() {
        val external = JournalpostTestData.inngaaendeDokument(dokumenter = null)

        val result = runCatching {
            external.toInternal(dummyIdent)
        }

        result.isFailure shouldBe true
        result.exceptionOrNull().shouldBeInstanceOf<TransformationException>()
        val exception = result.exceptionOrNull() as TransformationException
        exception.context[TransformationException.feltnavnKey] shouldBe "dokumenter"
    }

}
