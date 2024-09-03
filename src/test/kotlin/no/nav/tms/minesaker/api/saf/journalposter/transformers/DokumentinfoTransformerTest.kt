package no.nav.tms.minesaker.api.saf.journalposter.transformers

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.tms.minesaker.api.saf.sakstemaer.SakstemaException
import no.nav.tms.minesaker.api.saf.journalposter.v1.Dokumenttype
import no.nav.tms.minesaker.api.saf.journalposter.v1.Dokumentvariant
import no.nav.tms.minesaker.api.saf.journalposter.v1.SafDokumentInfo
import no.nav.tms.minesaker.api.saf.journalposter.v1.SafDokumentvariant
import no.nav.tms.minesaker.api.saf.journalposter.v1.SafVariantformat
import no.nav.tms.minesaker.api.saf.journalposter.v1.toInternal
import org.junit.jupiter.api.Test

internal class DokumentinfoTransformerTest {

    @Test
    fun `Skal markere forste dokument som hoveddokument, og resten som vedlegg`() {
        val externals = treGyldigeDokumenter()

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
        val externals = listOf(
            SafDokumentInfo(
                tittel = "Dummytittel uten arkiverte varianger",
                dokumentInfoId = "dummyId004",
                dokumentvarianter = emptyList()
            )
        )

        val result = runCatching {
            externals.toInternal()
        }

        result.isFailure shouldBe true
        result.exceptionOrNull().shouldBeInstanceOf<SakstemaException>()
        val exception = result.exceptionOrNull() as SakstemaException
        exception.context["feltnavn"] shouldBe "dokumentvarianter"
    }

    @Test
    fun `Velg preferer alltid SLADDET-variant hvis den varianten finnes`() {
        val externals = listOf(
            SafDokumentInfo(
                tittel = "Med sladdet og arkivert variant",
                dokumentInfoId = "dummyId005",
                dokumentvarianter = listOf(
                    sladdetVariant(),
                    arkivertVariant()
                )
            )
        )

        val internals = externals.toInternal()

        internals.shouldNotBeNull()
        internals.size shouldBe 1
        internals.first().variant shouldBe Dokumentvariant.SLADDET
    }

    @Test
    fun `Skal takle at tittel ikke er tilgjengelig i SAF, return dummy tittel til sluttbruker`() {

        val externals = listOf(
            SafDokumentInfo(
                tittel = null,
                dokumentInfoId = "dummyId002",
                dokumentvarianter = listOf(arkivertVariant())
            )
        )

        externals.toInternal()[0].tittel shouldBe "Uten tittel"
    }

}

private fun treGyldigeDokumenter(): List<SafDokumentInfo> {
    return listOf(
        dokument("Hveddok", "dummyId5", arkivertVariant()),
        dokument("Vedlegg1", "dummyId6", sladdetVariant()),
        dokument("Vedlegg2", "dummyId7", arkivertVariant())
    )
}

private fun dokument(
    tittel: String = "Dummytittel gyldig dokument 11",
    dokumentInfoId: String = "dummyId011",
    variant: SafDokumentvariant = arkivertVariant()
): SafDokumentInfo {
    return SafDokumentInfo(tittel, dokumentInfoId, listOf(variant))
}


private fun arkivertVariant() =
    SafDokumentvariant(SafVariantformat.ARKIV, true, listOf("ok"), "PDF")

private fun sladdetVariant() =
    SafDokumentvariant(SafVariantformat.SLADDET, true, listOf("Skannet_dokument"), "PDF")
