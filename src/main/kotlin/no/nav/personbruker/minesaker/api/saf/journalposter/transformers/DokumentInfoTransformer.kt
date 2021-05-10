package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.common.exception.TransformationException
import no.nav.personbruker.minesaker.api.saf.domain.DokumentInfoId
import no.nav.personbruker.minesaker.api.saf.domain.Dokumentinfo
import no.nav.personbruker.minesaker.api.saf.domain.Dokumenttype
import no.nav.personbruker.minesaker.api.saf.domain.Tittel
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger(HentJournalposter.DokumentInfo::class.java)

/**
 * Er kun interessert i den arkivert versjonen av dokumentet, da det er denne som er mest vanlig.
 */
fun List<HentJournalposter.DokumentInfo?>.toInternal(): List<Dokumentinfo> {
    val internals = mutableListOf<Dokumentinfo>()
    filterNotNull()
        .forEachIndexed { index, externalDokument ->
            externalDokument.kastFeilHvisManglerVarianter()
            externalDokument.loggHvisHarFlereVariangerEnnForventet()
            val forsteVariant = externalDokument.dokumentvarianter.filterNotNull().first()
            val dokumenttype = avgjorDokumenttype(index)
            val internal = externalDokument.toInternal(forsteVariant, dokumenttype)
            internals.add(internal)
        }
    return internals
}

private fun HentJournalposter.DokumentInfo.kastFeilHvisManglerVarianter() {
    if (utenVarianter()) {
        throw TransformationException.withMissingFieldName("dokumentvarianter")
    }
}

private fun HentJournalposter.DokumentInfo.utenVarianter() =
    dokumentvarianter.isEmpty()

private fun HentJournalposter.DokumentInfo.loggHvisHarFlereVariangerEnnForventet() {
    if (flereVarianterEnnForventet()) {
        val varianter = dokumentvarianter.map { variant -> variant?.variantformat }.toList()
        log.warn("Det ble sendt med mer enn en dokumentvariant: $varianter. Dette kan indikere en endring i SAF. Returnerer første variant: ${varianter[0]}.")
    }
}

private fun HentJournalposter.DokumentInfo.flereVarianterEnnForventet() =
    dokumentvarianter.size > 1

private fun avgjorDokumenttype(index: Int) = if (index == 0) {
    Dokumenttype.HOVED
} else {
    Dokumenttype.VEDLEGG
}

fun HentJournalposter.DokumentInfo.toInternal(
    externalVariant: HentJournalposter.Dokumentvariant,
    dokumenttype: Dokumenttype
): Dokumentinfo {
    val brukerHarTilgang = externalVariant.brukerHarTilgang == true
    val eventuelleGrunnerTilManglendeTilgang =
        plukkUtEventuelleGrunnerTilManglendeTilgang(brukerHarTilgang, externalVariant)
    return Dokumentinfo(
        Tittel(tittel ?: "Uten tittel"),
        DokumentInfoId(dokumentInfoId),
        dokumenttype,
        brukerHarTilgang,
        eventuelleGrunnerTilManglendeTilgang
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
