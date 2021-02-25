package no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter

object DokumentVariantObjectMother {

    fun giveMeArkivertVariant(): HentJournalposter.Dokumentvariant {
        return HentJournalposter.Dokumentvariant(HentJournalposter.Variantformat.ARKIV, "dummyUuid1", true)
    }

    fun giveMeArkivertVariantUtenFiluuid(): HentJournalposter.Dokumentvariant {
        return HentJournalposter.Dokumentvariant(HentJournalposter.Variantformat.ARKIV, null, true)
    }

    fun giveMeArkivertVariantUtenBrukerHarTilgangSatt(): HentJournalposter.Dokumentvariant {
        return HentJournalposter.Dokumentvariant(HentJournalposter.Variantformat.ARKIV, "dummyUuid2", null)
    }

    fun giveMeOriginalVariant(): HentJournalposter.Dokumentvariant {
        return HentJournalposter.Dokumentvariant(HentJournalposter.Variantformat.ORIGINAL, "dummyUuid3", true)
    }

    fun giveMeSladdetVariant(): HentJournalposter.Dokumentvariant {
        return HentJournalposter.Dokumentvariant(HentJournalposter.Variantformat.SLADDET, "dummyUuid4", true)
    }

}
