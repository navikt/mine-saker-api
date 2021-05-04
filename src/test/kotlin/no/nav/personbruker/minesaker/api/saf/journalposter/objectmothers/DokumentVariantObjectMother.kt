package no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter

object DokumentVariantObjectMother {

    private val ingenFeilkode = listOf("ok")
    
    fun giveMeArkivertVariant(): HentJournalposter.Dokumentvariant {
        return HentJournalposter.Dokumentvariant(HentJournalposter.Variantformat.ARKIV, true, ingenFeilkode)
    }

    fun giveMeArkivertVariantUtenDokumentInfoId(): HentJournalposter.Dokumentvariant {
        return HentJournalposter.Dokumentvariant(HentJournalposter.Variantformat.ARKIV, true, ingenFeilkode)
    }

    fun giveMeArkivertVariantUtenBrukerHarTilgangSatt(): HentJournalposter.Dokumentvariant {
        val feilkode = listOf("Skannet_dokument")
        return HentJournalposter.Dokumentvariant(HentJournalposter.Variantformat.ARKIV, null, feilkode)
    }

    fun giveMeOriginalVariant(): HentJournalposter.Dokumentvariant {
        return HentJournalposter.Dokumentvariant(HentJournalposter.Variantformat.ORIGINAL, true, ingenFeilkode)
    }

    fun giveMeSladdetVariant(): HentJournalposter.Dokumentvariant {
        val feilkode = listOf("Skannet_dokument")
        return HentJournalposter.Dokumentvariant(HentJournalposter.Variantformat.SLADDET, true, feilkode)
    }

}
