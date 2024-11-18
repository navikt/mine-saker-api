package no.nav.tms.minesaker.api.saf.journalposter.v2

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dokument.saf.selvbetjening.generated.dto.ALLE_JOURNALPOSTER
import no.nav.dokument.saf.selvbetjening.generated.dto.AlleJournalposter
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

fun AlleJournalposter.Result.toInternal(): List<JournalpostV2> {
    return dokumentoversiktSelvbetjening.journalposter.map {
        val (dokument, vedlegg) = dokumenter(it.dokumenter).let { dokumenter ->
            dokumenter.first() to dokumenter.drop(1)
        }

        val sakstema = Sakstema.fromExternal(it.tema!!)

        JournalpostV2(
            journalpostId = it.journalpostId,
            tittel = it.tittel ?: "---",
            temakode = sakstema.name,
            temanavn = sakstema.navn,
            avsender = mapAvsender(it.avsender, it.mottaker, it.journalposttype),
            mottaker = mapMottaker(it.mottaker, it.avsender, it.journalposttype),
            journalposttype = mapJournalpostType(it.journalposttype),
            opprettet = opprettet(it.relevanteDatoer),
            dokument = dokument,
            vedlegg = vedlegg
        )
    }
}

private fun mapJournalpostType(type: SafJournalposttypeV2) = when(type) {
    SafJournalposttypeV2.I -> "Inn"
    SafJournalposttypeV2.U -> "Ut"
    else -> "Notat"
}

private fun mapAvsender(avsender: AvsenderMottaker?, mottaker: AvsenderMottaker?, type: SafJournalposttypeV2): String? {
    return when {
        avsender == null && mottaker == null && type == SafJournalposttypeV2.U -> "NAV"
        avsender != null -> avsender.navn
        else -> null
    }
}

private fun mapMottaker(mottaker: AvsenderMottaker?, avsender: AvsenderMottaker?, type: SafJournalposttypeV2): String? {
    return when {
        mottaker == null && avsender == null && type == SafJournalposttypeV2.I -> "NAV"
        mottaker != null -> mottaker.navn
        else -> null
    }
}

private val log = KotlinLogging.logger {}

private fun dokumenter(dokumenter: List<DokumentInfo?>?): List<DokumentHeaderV2> {

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
                        filtype = variant.filtype,
                        filstorrelse = variant.filstorrelse,
                        brukerHarTilgang = variant.brukerHarTilgang,
                        tilgangssperre = if (variant.brukerHarTilgang) {
                            null
                        } else {
                            variant.code
                                .filterNotNull()
                                .minOfOrNull { Tilgangssperre.parse(it) }
                        }
                    )
                } ?: run {
                log.warn { "Dokumentet med dokumentInfoId=${info.dokumentInfoId} har ingen varianter som kan vises for bruker." }
                null
            }
        } ?: run {
            log.warn { "Mottok journalpost uten dokumenter" }
            listOf(DokumentHeaderV2.blank())
        }
}

private fun opprettet(datoer: List<RelevantDato?>): ZonedDateTime {
    val opprettet = datoer.filterNotNull()
        .firstOrNull { it.datotype == Datotype.DATO_OPPRETTET }
        ?.dato
        ?.let { LocalDateTime.parse(it) }
        ?: throw IllegalArgumentException("Fant ikke opprettet dato for journalpost")

    return ZonedDateTime.of(opprettet, ZoneId.of("Z"))
}
