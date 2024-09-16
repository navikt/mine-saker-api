package no.nav.tms.minesaker.api.saf.journalposter.v2

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dokument.saf.selvbetjening.generated.dto.ALLE_JOURNALPOSTER
import no.nav.dokument.saf.selvbetjening.generated.dto.AlleJournalposter
import no.nav.dokument.saf.selvbetjening.generated.dto.enums.AvsenderMottakerIdType
import no.nav.dokument.saf.selvbetjening.generated.dto.allejournalposter.AvsenderMottaker
import no.nav.dokument.saf.selvbetjening.generated.dto.allejournalposter.DokumentInfo
import no.nav.dokument.saf.selvbetjening.generated.dto.enums.Datotype
import no.nav.dokument.saf.selvbetjening.generated.dto.enums.Variantformat
import no.nav.dokument.saf.selvbetjening.generated.dto.allejournalposter.RelevantDato
import no.nav.tms.minesaker.api.saf.GraphQLRequest
import no.nav.tms.minesaker.api.saf.compactJson
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class AlleJournalposterRequest(override val variables: AlleJournalposterRequestVariables) : GraphQLRequest {

    override val query: String get() = queryString

    companion object {
        val queryString = compactJson(ALLE_JOURNALPOSTER)

        fun create(ident: String) = AlleJournalposterRequest(
            AlleJournalposterRequestVariables(ident)
        )
    }
}

data class AlleJournalposterRequestVariables(
    val ident: String

)

fun AlleJournalposter.Result.toInternal(innloggetBruker: String): List<JournalpostV2> {
    return dokumentoversiktSelvbetjening.journalposter.map {
        JournalpostV2(
            journalpostId = it.journalpostId,
            tittel = it.tittel ?: "---",
            journalposttype = it.journalposttype.mapToInternal(),
            journalstatus = it.journalstatus?.format() ?: "---",
            avsender = mapAvsender(it.avsender, it.mottaker, it.journalposttype, innloggetBruker),
            mottaker = mapMottaker(it.mottaker, it.avsender, it.journalposttype, innloggetBruker),
            opprettet = opprettet(it.relevanteDatoer),
            dokumenter = dokumenter(it.dokumenter),
            sakstema = it.tema?.let { tema -> Sakstema.fromExternal(tema) }
        )
    }
}

private fun mapAvsender(avsender: AvsenderMottaker?, mottaker: AvsenderMottaker?, type: SafJournalposttypeV2, innloggetBruker: String): AvsenderMottakerV2? {
    return when (val it = mapAvsenderMottaker(avsender, innloggetBruker)) {
        null -> if (mottaker == null && type == SafJournalposttypeV2.U) {
            AvsenderMottakerV2(type = AvsenderMottakerTypeV2.NAV, navn = "NAV")
        } else {
            null
        }
        else -> AvsenderMottakerV2(type = it, navn = avsender?.navn ?: "---")
    }
}

private fun mapMottaker(mottaker: AvsenderMottaker?, avsender: AvsenderMottaker?, type: SafJournalposttypeV2, innloggetBruker: String): AvsenderMottakerV2? {
    return when (val it = mapAvsenderMottaker(mottaker, innloggetBruker)) {
        null -> if (avsender == null && type == SafJournalposttypeV2.I) {
            AvsenderMottakerV2(type = AvsenderMottakerTypeV2.NAV, navn = "NAV")
        } else {
            null
        }
        else -> AvsenderMottakerV2(type = it, navn = mottaker?.navn ?: "---")
    }
}

private fun mapAvsenderMottaker(avsenderMottaker: AvsenderMottaker?, innloggetBruker: String) = when (avsenderMottaker?.type) {
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

private val log = KotlinLogging.logger {}

private fun dokumenter(dokumenter: List<DokumentInfo?>?): List<DokumentHeaderV2> {

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

private fun opprettet(datoer: List<RelevantDato?>): ZonedDateTime {
    val opprettet = datoer.filterNotNull()
        .firstOrNull { it.datotype == Datotype.DATO_OPPRETTET }
        ?.dato
        ?.let { LocalDateTime.parse(it) }
        ?: throw IllegalArgumentException("Fant ikke opprettet dato for journalpost")

    return ZonedDateTime.of(opprettet, ZoneId.of("Z"))
}
