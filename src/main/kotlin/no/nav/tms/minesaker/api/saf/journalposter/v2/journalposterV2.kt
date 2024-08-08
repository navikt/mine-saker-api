package no.nav.tms.minesaker.api.saf.journalposter.v2

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposterV2
import no.nav.dokument.saf.selvbetjening.generated.dto.enums.AvsenderMottakerIdType
import no.nav.dokument.saf.selvbetjening.generated.dto.enums.Datotype
import no.nav.dokument.saf.selvbetjening.generated.dto.enums.Variantformat
import no.nav.dokument.saf.selvbetjening.generated.dto.hentjournalposterv2.AvsenderMottaker
import no.nav.dokument.saf.selvbetjening.generated.dto.hentjournalposterv2.DokumentInfo
import no.nav.dokument.saf.selvbetjening.generated.dto.hentjournalposterv2.RelevantDato
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

private val log = KotlinLogging.logger {}

data class HentJournalposterResponseV2(
    val kode: String,
    val navn: String,

    val journalposter: List<JournalpostV2>
)

data class JournalpostV2(
    val journalpostId: String,
    val tittel: String,
    val journalposttype: JournalposttypeV2,
    val journalstatus: String,
    @JsonIgnore val avsender: AvsenderMottakerV2,
    @JsonIgnore val mottaker: AvsenderMottakerV2,
    val opprettet: ZonedDateTime,
    val dokumenter: List<DokumentHeaderV2>
) {
    val avsendertype = avsender.type
    val avsendernavn = avsender.navn
    val mottakertype = mottaker.type
    val mottakernavn = mottaker.navn
}

data class DokumentHeaderV2(
    val dokumentInfoId: String,
    val tittel: String,
    val dokumenttype: DokumenttypeV2,
    val filtype: String,
    val filstorrelse: Int,
    val brukerHarTilgang: Boolean,
    val sladdet: Boolean
)

data class AvsenderMottakerV2(
    val type: AvsenderMottakerTypeV2,
    val navn: String
)

enum class AvsenderMottakerTypeV2 {
    NAV, Bruker, Person, Organisasjon, Helsepersonell, Internasjonal, Null, Ukjent
}

enum class JournalposttypeV2 {
    Inn, Ut, Notat
}

enum class DokumenttypeV2 {
    Hoved, Vedlegg
}

fun HentJournalposterV2.Result.toInternal(innloggetBruker: String): HentJournalposterResponseV2? {

    return dokumentoversiktSelvbetjening.tema.firstOrNull()?.let { tema ->
        val journalposter = tema.journalposter.filterNotNull().map {
            JournalpostV2(
                journalpostId = it.journalpostId,
                tittel = it.tittel ?: "---",
                journalposttype = it.journalposttype.mapToInternal(),
                journalstatus = it.journalstatus?.format() ?: "---",
                avsender = avsenderMottaker(it.avsender, innloggetBruker),
                mottaker = avsenderMottaker(it.mottaker, innloggetBruker),
                opprettet = opprettet(it.relevanteDatoer),
                dokumenter = dokumenter(it.dokumenter)
            )
        }

        HentJournalposterResponseV2(
            tema.kode,
            tema.navn,
            journalposter
        )
    }

}

private fun SafJournalposttypeV2.mapToInternal() = when (this) {
    SafJournalposttypeV2.I -> JournalposttypeV2.Inn
    SafJournalposttypeV2.U -> JournalposttypeV2.Ut
    SafJournalposttypeV2.N -> JournalposttypeV2.Notat
    else -> throw IllegalArgumentException("Kjenner ikke igjen journalposttype $this")
}

private fun SafJournalstatusV2.format() = this.name.lowercase()
    .replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
    .replace("_([a-z])".toRegex()) {
        it.destructured.let { (c) -> c.uppercase() }
    }

fun avsenderMottaker(avsenderMottaker: AvsenderMottaker?, innloggetBruker: String) = when (avsenderMottaker?.type) {
    null -> AvsenderMottakerTypeV2.NAV
    AvsenderMottakerIdType.FNR -> if (avsenderMottaker.id == innloggetBruker) {
        AvsenderMottakerTypeV2.Bruker
    } else {
        AvsenderMottakerTypeV2.Person
    }

    AvsenderMottakerIdType.ORGNR -> AvsenderMottakerTypeV2.Organisasjon
    AvsenderMottakerIdType.HPRNR -> AvsenderMottakerTypeV2.Helsepersonell
    AvsenderMottakerIdType.UTL_ORG -> AvsenderMottakerTypeV2.Internasjonal
    AvsenderMottakerIdType.NULL -> AvsenderMottakerTypeV2.Null
    AvsenderMottakerIdType.UKJENT -> AvsenderMottakerTypeV2.Ukjent
    else -> throw IllegalArgumentException("Fant ikke mapping for AvsenderMottakerIdType ${avsenderMottaker.type}")
}.let {
    when (it) {
        AvsenderMottakerTypeV2.NAV -> AvsenderMottakerV2(type = it, navn = "NAV")
        else -> AvsenderMottakerV2(type = it, navn = avsenderMottaker?.navn ?: "---")
    }
}

fun opprettet(datoer: List<RelevantDato?>): ZonedDateTime {
    val opprettet = datoer.filterNotNull()
        .firstOrNull { it.datotype == Datotype.DATO_OPPRETTET }
        ?.dato
        ?.let { LocalDateTime.parse(it) }
        ?: throw IllegalArgumentException("Fant ikke opprettet dato for journalpost")

    return ZonedDateTime.of(opprettet, ZoneId.of("Z"))
}

fun dokumenter(dokumenter: List<DokumentInfo?>?): List<DokumentHeaderV2> {

    val dokumentType = DecayingToggle(DokumenttypeV2.Hoved, DokumenttypeV2.Vedlegg)

    return dokumenter
        ?.filterNotNull()
        ?.mapNotNull { info ->
            info.dokumentvarianter
                .filterNotNull()
                .let { varianter ->
                    varianter.firstOrNull { variant ->
                        variant.variantformat == Variantformat.SLADDET
                    } ?: varianter.firstOrNull { variant ->
                        variant.variantformat == Variantformat.ARKIV
                    }
                }?.let { variant ->
                    DokumentHeaderV2(
                        dokumentInfoId = info.dokumentInfoId,
                        tittel = info.tittel ?: "---",
                        dokumenttype = dokumentType.value,
                        filtype = variant.filtype,
                        filstorrelse = variant.filstorrelse,
                        brukerHarTilgang = variant.brukerHarTilgang,
                        sladdet = variant.variantformat == Variantformat.SLADDET
                    )
                } ?: run {
                log.warn { "Dokumentet med dokumentInfoId=${info.dokumentInfoId} har ingen varianter som kan vises for bruker." }
                null
            }
        } ?: emptyList()
}



private class DecayingToggle<T>(private val initial: T, private val fallback: T) {
    private var isDecayed = false

    val value: T get() =
        if (!isDecayed) {
            isDecayed = true
            initial
        } else {
            fallback
        }
}
