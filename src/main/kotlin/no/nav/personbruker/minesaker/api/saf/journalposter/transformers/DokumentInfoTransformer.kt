package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter.Variantformat.ARKIV
import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter.Variantformat.SLADDET
import no.nav.personbruker.minesaker.api.common.exception.TransformationException
import no.nav.personbruker.minesaker.api.domain.*
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger(HentJournalposter.DokumentInfo::class.java)

fun List<HentJournalposter.DokumentInfo?>.toInternal(): List<Dokumentinfo> {
    val internals = mutableListOf<Dokumentinfo>()
    filterNotNull()
        .forEachIndexed { index, externalDokument ->
            externalDokument.kastFeilHvisManglerVarianter()
            val valgtVariant = externalDokument.velgSladdetVariantOverArkivertVariant()
            if(valgtVariant != null) {
                val dokumenttype = avgjorDokumenttype(index)
                val internal = externalDokument.toInternal(valgtVariant, dokumenttype)
                internals.add(internal)

            } else {
                val msg = "Dokumentet med dokumentInfoId={} har ingen dokumenttype som kan vises for sluttbruker."
                log.warn(msg, externalDokument.dokumentInfoId)
            }
        }
    return internals
}

private fun HentJournalposter.DokumentInfo.velgSladdetVariantOverArkivertVariant(): HentJournalposter.Dokumentvariant? {
    var variant = dokumentvarianter.find { v -> v?.variantformat == SLADDET }
    if (variant == null) {
        variant = dokumentvarianter.find { v -> v?.variantformat == ARKIV }
    }
    return variant
}

private fun HentJournalposter.DokumentInfo.kastFeilHvisManglerVarianter() {
    if (utenVarianter()) {
        throw TransformationException.withMissingFieldName("dokumentvarianter")
    }
}

private fun HentJournalposter.DokumentInfo.utenVarianter() =
    dokumentvarianter.isEmpty()

private fun avgjorDokumenttype(index: Int) = if (index == 0) {
    Dokumenttype.HOVED
} else {
    Dokumenttype.VEDLEGG
}

fun HentJournalposter.DokumentInfo.toInternal(
    externalVariant: HentJournalposter.Dokumentvariant,
    dokumenttype: Dokumenttype
): Dokumentinfo {
    val eventuelleGrunnerTilManglendeTilgang =
        plukkUtEventuelleGrunnerTilManglendeTilgang(externalVariant.brukerHarTilgang, externalVariant)
    return Dokumentinfo(
        Tittel(tittel ?: "Uten tittel"),
        DokumentInfoId(dokumentInfoId),
        dokumenttype,
        externalVariant.brukerHarTilgang,
        eventuelleGrunnerTilManglendeTilgang,
        externalVariant.variantformat.toInternal()
    )
}

private fun plukkUtEventuelleGrunnerTilManglendeTilgang(
    brukerHarTilgang: Boolean,
    externalVariant: HentJournalposter.Dokumentvariant
): List<String> = if (brukerHarTilgang) {
    emptyList()
} else {
    externalVariant.code.filterNotNull()
}
