package no.nav.tms.minesaker.api.saf.journalposter.v1

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dokument.saf.selvbetjening.generated.dto.enums.Variantformat
import no.nav.tms.minesaker.api.saf.sakstemaer.SakstemaException

data class Dokumentinfo(
    val tittel: String,
    val dokumentInfoId: String,
    val dokumenttype: Dokumenttype,
    val brukerHarTilgang: Boolean,
    val eventuelleGrunnerTilManglendeTilgang: List<String>,
    val variant: Dokumentvariant,
    val filtype: String
)

data class Dokumentkilde(
    val innloggetBrukerErSelvKilden: Boolean,
    val type: DokumentkildeType
)
enum class DokumentkildeType {
    PERSON,
    ORGANISASJON,
    HELSEPERSONELL,
    UKJENT
}

enum class Dokumenttype {
    HOVED,
    VEDLEGG
}
enum class Dokumentvariant {
    SLADDET,
    ARKIV
}

private val log = KotlinLogging.logger {}

fun List<SafDokumentInfo?>.toInternal(): List<Dokumentinfo> {
    return filterNotNull()
        .mapIndexedNotNull { index, externalDokument ->
            externalDokument.kastFeilHvisManglerVarianter()
            val valgtVariant = externalDokument.velgSladdetVariantOverArkivertVariant()

            if (valgtVariant != null) {
                val dokumenttype = if (index == 0) {
                    Dokumenttype.HOVED
                } else {
                    Dokumenttype.VEDLEGG
                }

                externalDokument.toInternal(valgtVariant, dokumenttype)
            } else {
                log.warn { "Dokumentet med dokumentInfoId=${externalDokument.dokumentInfoId} har ingen dokumenttype som kan vises for sluttbruker." }
                null
            }
        }
}

fun SafDokumentInfo.velgSladdetVariantOverArkivertVariant(): SafDokumentvariant? {
    var variant = dokumentvarianter.find { v -> v?.variantformat == SafVariantformat.SLADDET }
    if (variant == null) {
        variant = dokumentvarianter.find { v -> v?.variantformat == SafVariantformat.ARKIV }
    }
    return variant
}

fun SafDokumentInfo.kastFeilHvisManglerVarianter() {
    if (dokumentvarianter.isEmpty()) {
        throw SakstemaException.withMissingFieldName("dokumentvarianter")
    }
}

fun SafDokumentInfo.toInternal(
    externalVariant: SafDokumentvariant,
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
        externalVariant.variantformat.toInternal(),
        externalVariant.filtype
    )
}

private fun plukkUtEventuelleGrunnerTilManglendeTilgang(
    brukerHarTilgang: Boolean,
    externalVariant: SafDokumentvariant
): List<String> = if (brukerHarTilgang) {
    emptyList()
} else {
    externalVariant.code.filterNotNull()
}

fun Variantformat.toInternal(): Dokumentvariant {
    return when (this) {
        Variantformat.ARKIV -> Dokumentvariant.ARKIV
        Variantformat.SLADDET -> Dokumentvariant.SLADDET
        else -> {
            val msg = "Klarte ikke å konvertere dokumentvariant"
            val exception = SakstemaException(msg, SakstemaException.ErrorType.INVALID_STATE)
            exception.addContext("funnetVariantformat", this)
            throw exception
        }
    }
}

