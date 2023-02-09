package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.personbruker.minesaker.api.common.exception.TransformationException
import no.nav.personbruker.minesaker.api.domain.Dokumenttype
import no.nav.personbruker.minesaker.api.domain.Dokumentvariant
import no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers.DokumentInfoObjectMother
import org.junit.jupiter.api.Test

internal class DokumentinfoTransformerTest {

    @Test
    fun `Skal markere forste dokument som hoveddokument, og resten som vedlegg`() {
        val externals = DokumentInfoObjectMother.giveMeTreGyldigeDokumenter()

        val internals = externals.toInternal()

        internals.shouldNotBeNull()
        internals.size shouldBe externals.size
        internals[0].dokumenttype shouldBe Dokumenttype.HOVED
        internals[0].tittel shouldBe externals[0].tittel
        internals[1].dokumenttype shouldBe Dokumenttype.VEDLEGG
        internals[1].tittel shouldBe externals[1].tittel
        internals[2].dokumenttype shouldBe Dokumenttype.VEDLEGG
        internals[2].tittel shouldBe externals[2].tittel
    }

    @Test
    fun `Kaste feil hvis dokument ikke har varianter`() {
        val externals = listOf(DokumentInfoObjectMother.giveMeDokumentUtenNoenVarianter())

        val result = runCatching {
            externals.toInternal()
        }

        result.isFailure shouldBe true
        result.exceptionOrNull().shouldBeInstanceOf<TransformationException>()
        val exception = result.exceptionOrNull() as TransformationException
        exception.context["feltnavn"] shouldBe "dokumentvarianter"
    }

    @Test
    fun `Velg preferer alltid SLADDET-variant hvis den varianten finnes`() {
        val externals = listOf(DokumentInfoObjectMother.giveMeDokumentMedSladdetOgArkivertVariant())

        val internals = externals.toInternal()

        internals.shouldNotBeNull()
        internals.size shouldBe 1
        internals.first().variant shouldBe Dokumentvariant.SLADDET
    }

    @Test
    fun `Skal takle at tittel ikke er tilgjengelig i SAF, return dummy tittel til sluttbruker`() {
        val externals = listOf(DokumentInfoObjectMother.giveMeDokumentMedArkivertVariantMenUtenTittel())

        val result = externals.toInternal()

        result[0].tittel shouldBe "Uten tittel"
    }

}
