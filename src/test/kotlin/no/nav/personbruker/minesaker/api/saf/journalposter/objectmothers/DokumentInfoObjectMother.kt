package no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers

import no.nav.personbruker.minesaker.api.saf.journalposter.transformers.GraphQLDokumentInfo
import no.nav.personbruker.minesaker.api.saf.journalposter.transformers.GraphQLDokumentvariant

object DokumentInfoObjectMother {

    fun giveMeDokumentMedArkivertVariant(): GraphQLDokumentInfo {
        val varianter = listOf(
            DokumentVariantObjectMother.giveMeSladdetVariant(),
            DokumentVariantObjectMother.giveMeArkivertVariant()
        )
        val dokumentInfoId = "dummyId001"
        return GraphQLDokumentInfo("Dummytittel med arkivert", dokumentInfoId, varianter)
    }

    fun giveMeDokumentMedArkivertVariantMenUtenTittel(): GraphQLDokumentInfo {
        val varianter = listOf(
            DokumentVariantObjectMother.giveMeArkivertVariant()
        )
        val dokumentInfoId = "dummyId002"
        return GraphQLDokumentInfo(null, dokumentInfoId, varianter)
    }

    fun giveMeDokumentUtenNoenVarianter(): GraphQLDokumentInfo {
        val dokumentInfoId = "dummyId004"
        return GraphQLDokumentInfo("Dummytittel uten arkiverte varianger", dokumentInfoId, emptyList())
    }

    fun giveMeDokumentMedSladdetOgArkivertVariant(): GraphQLDokumentInfo {
        val varianter = listOf(
            DokumentVariantObjectMother.giveMeSladdetVariant(),
            DokumentVariantObjectMother.giveMeArkivertVariant()
        )
        val dokumentInfoId = "dummyId005"
        val tittel = "Dummytittel medflere dokument varianter"
        return GraphQLDokumentInfo(tittel, dokumentInfoId, varianter)
    }

    fun giveMeTreGyldigeDokumenter(): List<GraphQLDokumentInfo> {
        return listOf(
            giveMeDokument("Hveddok", "dummyId5", DokumentVariantObjectMother.giveMeArkivertVariant()),
            giveMeDokument("Vedlegg1", "dummyId6", DokumentVariantObjectMother.giveMeSladdetVariant()),
            giveMeDokument("Vedlegg2", "dummyId7", DokumentVariantObjectMother.giveMeArkivertVariant())
        )
    }

    private fun giveMeDokument(
        tittel: String = "Dummytittel gyldig dokument 11",
        dokumentInfoId: String = "dummyId011",
        variant: GraphQLDokumentvariant = DokumentVariantObjectMother.giveMeArkivertVariant()
    ): GraphQLDokumentInfo {
        return GraphQLDokumentInfo(tittel, dokumentInfoId, listOf(variant))
    }

}
