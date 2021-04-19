package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.saf.domain.DokumentInfoId
import no.nav.personbruker.minesaker.api.saf.domain.Dokumentinfo
import no.nav.personbruker.minesaker.api.saf.domain.Tittel

/**
 * Er kun interessert i den arkivert versjonen av dokumentet, da det er denne som er mest vanlig.
 */
fun List<HentJournalposter.DokumentInfo?>.toInternal(): List<Dokumentinfo> {
    val internals = mutableListOf<Dokumentinfo>()
    filterNotNull()
        .forEach { externalDokument ->
            val externalArkivertDokumentVariant = externalDokument.getEventuellArkivertVariant()
            if (externalArkivertDokumentVariant != null) {
                val internal = externalDokument.toInternal(externalArkivertDokumentVariant)
                internals.add(internal)
            }
        }
    return internals
}

fun HentJournalposter.DokumentInfo.getEventuellArkivertVariant(): HentJournalposter.Dokumentvariant? {
    dokumentvarianter.forEach { variant ->
        if (variant?.hasArkivertVariant() == true) {
            return variant
        }
    }
    return null
}

fun HentJournalposter.DokumentInfo.toInternal(externalVariant: HentJournalposter.Dokumentvariant): Dokumentinfo {
    return Dokumentinfo(
        Tittel(tittel ?: "Uten tittel"),
        DokumentInfoId(dokumentInfoId),
        externalVariant.brukerHarTilgang == true
    )
}

fun HentJournalposter.Dokumentvariant.hasArkivertVariant(): Boolean {
    return variantformat === HentJournalposter.Variantformat.ARKIV
}
