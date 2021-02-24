package no.nav.personbruker.minesaker.api.saf.domain

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter

object DokumentInfoObjectMother {

    fun giveMeDokumentUtenArkivertVariant(): HentJournalposter.DokumentInfo {
        val varianter = listOf(
            DokumentVariantObjectMother.giveMeOriginalVariant(),
            DokumentVariantObjectMother.giveMeSladdetVariant()
        )
        return HentJournalposter.DokumentInfo("Dummytittel uten arkivert", varianter)
    }

    fun giveMeDokumentMedArkivertVariant(): HentJournalposter.DokumentInfo {
        val varianter = listOf(
            DokumentVariantObjectMother.giveMeOriginalVariant(),
            DokumentVariantObjectMother.giveMeSladdetVariant(),
            DokumentVariantObjectMother.giveMeArkivertVariant()
        )
        return HentJournalposter.DokumentInfo("Dummytittel med arkivert", varianter)
    }

    fun giveMeDokumentMedArkivertVariantMenUtenTittel(): HentJournalposter.DokumentInfo {
        val varianter = listOf(
            DokumentVariantObjectMother.giveMeArkivertVariant()
        )
        return HentJournalposter.DokumentInfo(null, varianter)
    }

    fun giveMeDokumentMedArkivertVariantMenUtenFiluuid(): HentJournalposter.DokumentInfo {
        val varianter = listOf(
            DokumentVariantObjectMother.giveMeArkivertVariantUtenFiluuid()
        )
        return HentJournalposter.DokumentInfo("Dummytittel med arkivert uten filuuid", varianter)
    }

    fun giveMeDokumentMedArkivertVariantMenUtenAtTilgangErSpesifisert(): HentJournalposter.DokumentInfo {
        val varianter = listOf(
            DokumentVariantObjectMother.giveMeArkivertVariantUtenBrukerHarTilgangSatt()
        )
        return HentJournalposter.DokumentInfo("Dummytittel med arkivert uten filuuid", varianter)
    }

}
