package no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter

object DokumentVariantObjectMother {

    fun giveMeArkivertVariant(): HentJournalposter.Dokumentvariant {
        return HentJournalposter.Dokumentvariant(HentJournalposter.Variantformat.ARKIV, true)
    }

    fun giveMeArkivertVariantUtenDokumentInfoId(): HentJournalposter.Dokumentvariant {
        return HentJournalposter.Dokumentvariant(HentJournalposter.Variantformat.ARKIV, true)
    }

    fun giveMeArkivertVariantUtenBrukerHarTilgangSatt(): HentJournalposter.Dokumentvariant {
        return HentJournalposter.Dokumentvariant(HentJournalposter.Variantformat.ARKIV, null)
    }

    fun giveMeOriginalVariant(): HentJournalposter.Dokumentvariant {
        return HentJournalposter.Dokumentvariant(HentJournalposter.Variantformat.ORIGINAL, true)
    }

    fun giveMeSladdetVariant(): HentJournalposter.Dokumentvariant {
        return HentJournalposter.Dokumentvariant(HentJournalposter.Variantformat.SLADDET, true)
    }

}
