package no.nav.tms.minesaker.api.domain

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

data class JournalpostV2(
    val journalpostId: String,
    val tittel: String,
    val journalposttype: JournalposttypeV2,
    val journalstatus: String,
    val temakode: Sakstemakode,
    val temanavn: String,
    val avsendertype: AvsenderMottakerTypeV2?,
    val avsender: String?,
    val mottakertype: AvsenderMottakerTypeV2?,
    val mottaker: String?,
    val opprettet: ZonedDateTime,
    val dokumenter: List<DokumentHeaderV2>
)

data class DokumentHeaderV2(
    val dokumentInfoId: String,
    val tittel: String,
    val dokumenttype: DokumenttypeV2,
    val filtype: String,
    val brukerHarTilgang: Boolean,
    val sladdet: Boolean
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
    val sakstemaer = dokumentoversiktSelvbetjening.tema

    return dokumentoversiktSelvbetjening.journalposter.map {
        JournalpostV2(
            journalpostId = it.journalpostId,
            tittel = it.tittel ?: "---",
            journalposttype = it.journalposttype.mapToInternal(),
            journalstatus = it.journalstatus?.format() ?: "---",
            temakode = Sakstemakode.valueOf(it.tema!!),
            temanavn = sakstemaer.navn(it.tema),
            avsendertype = avsenderMottakerType(it.avsender, innloggetBruker),
            avsender = it.avsender?.navn,
            mottakertype = avsenderMottakerType(it.mottaker, innloggetBruker),
            mottaker = it.mottaker?.navn,
            opprettet = opprettet(it.relevanteDatoer),
            dokumenter = dokumenter(it.dokumenter)
        )
    }
}

private fun List<SafSakstema>.navn(kode: String) = this.firstOrNull { it.kode == kode }?.navn ?: throw IllegalStateException("Fant ikke navn på sakstema ")

private fun SafJournalposttype.mapToInternal() = when(this) {
    SafJournalposttype.I -> JournalposttypeV2.Inn
    SafJournalposttype.U -> JournalposttypeV2.Ut
    SafJournalposttype.N -> JournalposttypeV2.Notat
    else -> throw  IllegalArgumentException("Kjenner ikke igjen journalposttype $this")
}

private fun SafJournalstatus.format() = this.name.lowercase()
    .replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
    .replace("_([a-z])".toRegex()) {
        it.destructured.let { (c) -> c.uppercase() }
    }

fun avsenderMottakerType(avsenderMottaker: AvsenderMottaker?, innloggetBruker: String) = when (avsenderMottaker?.type) {
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
        ?.map {
            val variant = it.dokumentvarianter
                .filterNotNull()
                .let { varianter ->
                    varianter.firstOrNull { variant ->
                        variant.variantformat == Variantformat.SLADDET
                    }?: varianter.firstOrNull { variant ->
                        variant.variantformat == Variantformat.ARKIV
                    }
                } ?: throw IllegalArgumentException("Dokumentet med dokumentInfoId={} har ingen varianter som kan vises for bruker.")

            DokumentHeaderV2(
                dokumentInfoId = it.dokumentInfoId,
                tittel = it.tittel ?: "---",
                dokumenttype = dokumentType.value,
                filtype = variant.filtype,
                brukerHarTilgang = variant.brukerHarTilgang,
                sladdet = variant.variantformat == Variantformat.SLADDET
            )
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
typealias SafSakstema = no.nav.dokument.saf.selvbetjening.generated.dto.allejournalposter.Sakstema
