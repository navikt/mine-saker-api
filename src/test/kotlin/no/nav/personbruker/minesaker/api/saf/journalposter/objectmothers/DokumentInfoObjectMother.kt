package no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter

object DokumentInfoObjectMother {

    fun giveMeDokumentUtenArkivertVariant(): HentJournalposter.DokumentInfo {
        val varianter = listOf(
            DokumentVariantObjectMother.giveMeOriginalVariant(),
            DokumentVariantObjectMother.giveMeSladdetVariant()
        )
        val dokumentInfoId = "dummyId001"
        return HentJournalposter.DokumentInfo("Dummytittel uten arkivert", dokumentInfoId, varianter)
    }

    fun giveMeDokumentMedArkivertVariant(): HentJournalposter.DokumentInfo {
        val varianter = listOf(
            DokumentVariantObjectMother.giveMeOriginalVariant(),
            DokumentVariantObjectMother.giveMeSladdetVariant(),
            DokumentVariantObjectMother.giveMeArkivertVariant()
        )
        val dokumentInfoId = "dummyId002"
        return HentJournalposter.DokumentInfo("Dummytittel med arkivert", dokumentInfoId, varianter)
    }

    fun giveMeDokumentMedArkivertVariantMenUtenTittel(): HentJournalposter.DokumentInfo {
        val varianter = listOf(
            DokumentVariantObjectMother.giveMeArkivertVariant()
        )
        val dokumentInfoId = "dummyId003"
        return HentJournalposter.DokumentInfo(null, dokumentInfoId, varianter)
    }

    fun giveMeDokumentMedArkivertVariantMenUtenDokumentInfoId(): HentJournalposter.DokumentInfo {
        val varianter = listOf(
            DokumentVariantObjectMother.giveMeArkivertVariantUtenDokumentInfoId()
        )
        val dokumentInfoId = "dummyId004"
        return HentJournalposter.DokumentInfo("Dummytittel med arkivert uten dokumentInfoId", dokumentInfoId, varianter)
    }

    fun giveMeDokumentMedArkivertVariantMenUtenAtTilgangErSpesifisert(): HentJournalposter.DokumentInfo {
        val varianter = listOf(
            DokumentVariantObjectMother.giveMeArkivertVariantUtenBrukerHarTilgangSatt()
        )
        val dokumentInfoId = "dummyId005"
        return HentJournalposter.DokumentInfo("Dummytittel med arkivert uten dokumentInfoId", dokumentInfoId, varianter)
    }

}
