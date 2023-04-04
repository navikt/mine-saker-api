package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import mu.KotlinLogging
import no.nav.personbruker.minesaker.api.exception.TransformationException
import no.nav.personbruker.minesaker.api.domain.Dokumentinfo
import no.nav.personbruker.minesaker.api.domain.Dokumenttype

private val log = KotlinLogging.logger {}

fun List<GraphQLDokumentInfo?>.toInternal(): List<Dokumentinfo> {
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

private fun GraphQLDokumentInfo.velgSladdetVariantOverArkivertVariant(): GraphQLDokumentvariant? {
    var variant = dokumentvarianter.find { v -> v?.variantformat == GraphQLVariantformat.SLADDET }
    if (variant == null) {
        variant = dokumentvarianter.find { v -> v?.variantformat == GraphQLVariantformat.ARKIV }
    }
    return variant
}

private fun GraphQLDokumentInfo.kastFeilHvisManglerVarianter() {
    if (utenVarianter()) {
        throw TransformationException.withMissingFieldName("dokumentvarianter")
    }
}

private fun GraphQLDokumentInfo.utenVarianter() =
    dokumentvarianter.isEmpty()

private fun avgjorDokumenttype(index: Int) = if (index == 0) {
    Dokumenttype.HOVED
} else {
    Dokumenttype.VEDLEGG
}

fun GraphQLDokumentInfo.toInternal(
    externalVariant: GraphQLDokumentvariant,
    dokumenttype: Dokumenttype
): Dokumentinfo {
    val eventuelleGrunnerTilManglendeTilgang =
        plukkUtEventuelleGrunnerTilManglendeTilgang(externalVariant.brukerHarTilgang, externalVariant)
    return Dokumentinfo(
        tittel ?: "Uten tittel",
        if (externalVariant.brukerHarTilgang) dokumentInfoId else "-",
        dokumenttype,
        externalVariant.brukerHarTilgang,
        eventuelleGrunnerTilManglendeTilgang,
        externalVariant.variantformat.toInternal()
    )
}

private fun plukkUtEventuelleGrunnerTilManglendeTilgang(
    brukerHarTilgang: Boolean,
    externalVariant: GraphQLDokumentvariant
): List<String> = if (brukerHarTilgang) {
    emptyList()
} else {
    externalVariant.code.filterNotNull()
}
