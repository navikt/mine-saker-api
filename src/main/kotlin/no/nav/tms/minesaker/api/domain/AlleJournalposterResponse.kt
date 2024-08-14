package no.nav.tms.minesaker.api.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dokument.saf.selvbetjening.generated.dto.AlleJournalposter
import no.nav.dokument.saf.selvbetjening.generated.dto.allejournalposter.AvsenderMottaker
import no.nav.dokument.saf.selvbetjening.generated.dto.allejournalposter.DokumentInfo
import no.nav.dokument.saf.selvbetjening.generated.dto.allejournalposter.RelevantDato
import no.nav.dokument.saf.selvbetjening.generated.dto.enums.AvsenderMottakerIdType
import no.nav.dokument.saf.selvbetjening.generated.dto.enums.Datotype
import no.nav.dokument.saf.selvbetjening.generated.dto.enums.Variantformat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

private val log = KotlinLogging.logger {}

data class JournalpostV2(
    val journalpostId: String,
    val tittel: String,
    val journalposttype: JournalposttypeV2,
    val journalstatus: String,
    @JsonIgnore val sakstema: Sakstema,
    @JsonIgnore val avsender: AvsenderMottakerV2?,
    @JsonIgnore val mottaker: AvsenderMottakerV2?,
    val opprettet: ZonedDateTime,
    val dokumenter: List<DokumentHeaderV2>
) {
    val temakode = sakstema.name
    val temanavn = sakstema.navn
    val avsendertype = avsender?.type
    val avsendernavn = avsender?.navn
    val mottakertype = mottaker?.type
    val mottakernavn = mottaker?.navn
}

data class DokumentHeaderV2(
    val dokumentInfoId: String,
    val tittel: String,
    val dokumenttype: DokumenttypeV2,
    val filtype: String,
    val brukerHarTilgang: Boolean,
    val sladdet: Boolean
)

data class AvsenderMottakerV2(
    val type: AvsenderMottakerTypeV2,
    val navn: String
)

enum class AvsenderMottakerTypeV2 {
    Bruker, Person, Organisasjon, Helsepersonell, Internasjonal, Null, Ukjent
}

enum class JournalposttypeV2 {
    Inn, Ut, Notat
}

enum class DokumenttypeV2 {
    Hoved, Vedlegg
}

fun AlleJournalposter.Result.toInternal(innloggetBruker: String): List<JournalpostV2> {

    return dokumentoversiktSelvbetjening.journalposter.map {
        JournalpostV2(
            journalpostId = it.journalpostId,
            tittel = it.tittel ?: "---",
            journalposttype = it.journalposttype.mapToInternal(),
            journalstatus = it.journalstatus?.format() ?: "---",
            sakstema = Sakstema.fromExternal(it.tema!!),
            avsender = avsenderMottaker(it.avsender, innloggetBruker),
            mottaker = avsenderMottaker(it.mottaker, innloggetBruker),
            opprettet = opprettet(it.relevanteDatoer),
            dokumenter = dokumenter(it.dokumenter)
        )
    }
}

private fun SafJournalposttype.mapToInternal() = when (this) {
    SafJournalposttype.I -> JournalposttypeV2.Inn
    SafJournalposttype.U -> JournalposttypeV2.Ut
    SafJournalposttype.N -> JournalposttypeV2.Notat
    else -> throw IllegalArgumentException("Kjenner ikke igjen journalposttype $this")
}

private fun SafJournalstatus.format() = this.name.lowercase()
    .replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
    .replace("_([a-z])".toRegex()) {
        it.destructured.let { (c) -> c.uppercase() }
    }

fun avsenderMottaker(avsenderMottaker: AvsenderMottaker?, innloggetBruker: String) = when (avsenderMottaker?.type) {
    null -> null
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
}?.let {
    AvsenderMottakerV2(type = it, navn = avsenderMottaker?.navn ?: "Ukjent")
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



typealias SafJournalposttype = no.nav.dokument.saf.selvbetjening.generated.dto.enums.Journalposttype
typealias SafJournalstatus = no.nav.dokument.saf.selvbetjening.generated.dto.enums.Journalstatus
