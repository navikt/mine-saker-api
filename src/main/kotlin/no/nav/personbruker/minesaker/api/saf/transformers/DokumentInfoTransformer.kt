package no.nav.personbruker.minesaker.api.saf.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.common.exception.MissingFieldException
import no.nav.personbruker.minesaker.api.saf.domain.Dokumentinfo

object DokumentInfoTransformer {

    /**
     * Er kun interessert i den arkivert versjonen av dokumentet, da det er denne som er mest vanlig.
     */
    fun toInternal(externals: List<HentJournalposter.DokumentInfo>): List<Dokumentinfo> {
        val internals = mutableListOf<Dokumentinfo>()
        externals.forEach { externalDokument ->
            val externalArkivertDokumentVariant = externalDokument.getEventuellArkivertVariant()
            if (externalArkivertDokumentVariant != null) {
                val internal = toInternal(externalDokument, externalArkivertDokumentVariant)
                internals.add(internal)
            }
        }
        return internals
    }

    private fun toInternal(
        external: HentJournalposter.DokumentInfo,
        externalVariant: HentJournalposter.Dokumentvariant
    ): Dokumentinfo {
        return Dokumentinfo(
            external.tittel ?: throw MissingFieldException("tittel"),
            externalVariant.filuuid ?: throw MissingFieldException("filuuid"),
            externalVariant.brukerHarTilgang == true
        )
    }

}

fun HentJournalposter.DokumentInfo.getEventuellArkivertVariant(): HentJournalposter.Dokumentvariant? {
    dokumentvarianter.forEach { variant ->
        if (variant?.hasArkivertVariant() == true) {
            return variant
        }
    }
    return null
}

fun HentJournalposter.Dokumentvariant.hasArkivertVariant(): Boolean {
    return variantformat === HentJournalposter.Variantformat.ARKIV
}
