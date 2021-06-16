package no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter

object DokumentInfoObjectMother {

    fun giveMeDokumentMedArkivertVariant(): HentJournalposter.DokumentInfo {
        val varianter = listOf(
            DokumentVariantObjectMother.giveMeSladdetVariant(),
            DokumentVariantObjectMother.giveMeArkivertVariant()
        )
        val dokumentInfoId = "dummyId001"
        return HentJournalposter.DokumentInfo("Dummytittel med arkivert", dokumentInfoId, varianter)
    }

    fun giveMeDokumentMedArkivertVariantMenUtenTittel(): HentJournalposter.DokumentInfo {
        val varianter = listOf(
            DokumentVariantObjectMother.giveMeArkivertVariant()
        )
        val dokumentInfoId = "dummyId002"
        return HentJournalposter.DokumentInfo(null, dokumentInfoId, varianter)
    }

    fun giveMeDokumentUtenNoenVarianter(): HentJournalposter.DokumentInfo {
        val dokumentInfoId = "dummyId004"
        return HentJournalposter.DokumentInfo("Dummytittel uten arkiverte varianger", dokumentInfoId, emptyList())
    }

    fun giveMeDokumentMedSladdetOgArkivertVariant(): HentJournalposter.DokumentInfo {
        val varianter = listOf(
            DokumentVariantObjectMother.giveMeSladdetVariant(),
            DokumentVariantObjectMother.giveMeArkivertVariant()
        )
        val dokumentInfoId = "dummyId005"
        val tittel = "Dummytittel medflere dokument varianter"
        return HentJournalposter.DokumentInfo(tittel, dokumentInfoId, varianter)
    }

    fun giveMeTreGyldigeDokumenter(): List<HentJournalposter.DokumentInfo> {
        return listOf(
            giveMeDokument("Hveddok", "dummyId5", DokumentVariantObjectMother.giveMeArkivertVariant()),
            giveMeDokument("Vedlegg1", "dummyId6", DokumentVariantObjectMother.giveMeSladdetVariant()),
            giveMeDokument("Vedlegg2", "dummyId7", DokumentVariantObjectMother.giveMeArkivertVariant())
        )
    }

    private fun giveMeDokument(
        tittel: String = "Dummytittel gyldig dokument 11",
        dokumentInfoId: String = "dummyId011",
        variant: HentJournalposter.Dokumentvariant = DokumentVariantObjectMother.giveMeArkivertVariant()
    ): HentJournalposter.DokumentInfo {
        return HentJournalposter.DokumentInfo(tittel, dokumentInfoId, listOf(variant))
    }

}
