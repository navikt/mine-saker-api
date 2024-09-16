package no.nav.tms.minesaker.api.saf.journalposter.v2

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dokument.saf.selvbetjening.generated.dto.HENT_JOURNALPOSTER_V2
import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposterV2
import no.nav.dokument.saf.selvbetjening.generated.dto.enums.AvsenderMottakerIdType
import no.nav.dokument.saf.selvbetjening.generated.dto.enums.Datotype
import no.nav.dokument.saf.selvbetjening.generated.dto.enums.Variantformat
import no.nav.dokument.saf.selvbetjening.generated.dto.hentjournalposterv2.AvsenderMottaker
import no.nav.dokument.saf.selvbetjening.generated.dto.hentjournalposterv2.DokumentInfo
import no.nav.dokument.saf.selvbetjening.generated.dto.hentjournalposterv2.RelevantDato
import no.nav.tms.minesaker.api.saf.GraphQLRequest
import no.nav.tms.minesaker.api.saf.compactJson
import no.nav.tms.minesaker.api.saf.sakstemaer.Sakstemakode
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class HentJournalposterV2Request(override val variables: HentJournalposterV2RequestVariables) : GraphQLRequest {

    override val query: String get() = queryString

    companion object {
        val queryString = compactJson(HENT_JOURNALPOSTER_V2)

        fun create(ident: String, sakstema: Sakstemakode) = HentJournalposterV2Request(
            HentJournalposterV2RequestVariables(ident, sakstema.name)
        )
    }
}

data class HentJournalposterV2RequestVariables(
    val ident: String,
    val sakstema: String

)

fun HentJournalposterV2.Result.toInternal(innloggetBruker: String): HentJournalposterResponseV2? {

    return dokumentoversiktSelvbetjening.tema.firstOrNull()?.let { tema ->
        val journalposter = tema.journalposter.filterNotNull().map {
            JournalpostV2(
                journalpostId = it.journalpostId,
                tittel = it.tittel ?: "---",
                journalposttype = it.journalposttype.mapToInternal(),
                journalstatus = it.journalstatus?.format() ?: "---",
                avsender = mapAvsender(it.avsender, it.mottaker, it.journalposttype, innloggetBruker),
                mottaker = mapMottaker(it.mottaker, it.avsender, it.journalposttype, innloggetBruker),
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

private fun opprettet(datoer: List<RelevantDato?>): ZonedDateTime {
    val opprettet = datoer.filterNotNull()
        .firstOrNull { it.datotype == Datotype.DATO_OPPRETTET }
        ?.dato
        ?.let { LocalDateTime.parse(it) }
        ?: throw IllegalArgumentException("Fant ikke opprettet dato for journalpost")

    return ZonedDateTime.of(opprettet, ZoneId.of("Z"))
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
