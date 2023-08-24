package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.personbruker.minesaker.api.exception.TransformationException
import no.nav.personbruker.minesaker.api.domain.Dokumenttype
import no.nav.personbruker.minesaker.api.domain.Dokumentvariant
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
            GraphQLDokumentInfo(
                tittel = "Dummytittel uten arkiverte varianger",
                dokumentInfoId = "dummyId004",
                dokumentvarianter = emptyList()
            )
        )

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
        val externals = listOf(
            GraphQLDokumentInfo(
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
            GraphQLDokumentInfo(
                tittel = null,
                dokumentInfoId = "dummyId002",
                dokumentvarianter = listOf(arkivertVariant())
            )
        )

        externals.toInternal()[0].tittel shouldBe "Uten tittel"
    }

}

private fun treGyldigeDokumenter(): List<GraphQLDokumentInfo> {
    return listOf(
        dokument("Hveddok", "dummyId5", arkivertVariant()),
        dokument("Vedlegg1", "dummyId6", sladdetVariant()),
        dokument("Vedlegg2", "dummyId7", arkivertVariant())
    )
}

private fun dokument(
    tittel: String = "Dummytittel gyldig dokument 11",
    dokumentInfoId: String = "dummyId011",
    variant: GraphQLDokumentvariant = arkivertVariant()
): GraphQLDokumentInfo {
    return GraphQLDokumentInfo(tittel, dokumentInfoId, listOf(variant))
}


private fun arkivertVariant() =
    GraphQLDokumentvariant(GraphQLVariantformat.ARKIV, true, listOf("ok"), "PDF")

private fun sladdetVariant() =
    GraphQLDokumentvariant(GraphQLVariantformat.SLADDET, true, listOf("Skannet_dokument"), "PDF")
