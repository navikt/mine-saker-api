package no.nav.tms.minesaker.api.journalpost.query

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dokument.saf.selvbetjening.generated.dto.ALLE_JOURNALPOSTER
import no.nav.dokument.saf.selvbetjening.generated.dto.AlleJournalposter
import no.nav.dokument.saf.selvbetjening.generated.dto.allejournalposter.AvsenderMottaker
import no.nav.dokument.saf.selvbetjening.generated.dto.allejournalposter.DokumentInfo
import no.nav.dokument.saf.selvbetjening.generated.dto.enums.Variantformat
import no.nav.dokument.saf.selvbetjening.generated.dto.enums.Journalposttype
import no.nav.tms.minesaker.api.journalpost.DokumentHeader
import no.nav.tms.minesaker.api.journalpost.Journalpost
import no.nav.tms.minesaker.api.journalpost.Sakstema
import no.nav.tms.minesaker.api.journalpost.Tilgangssperre
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

fun AlleJournalposter.Result.toInternal(): List<Journalpost> {
    return dokumentoversiktSelvbetjening.journalposter.map {
        val (dokument, vedlegg) = dokumenter(it.dokumenter).let { dokumenter ->
            dokumenter.first() to dokumenter.drop(1)
        }

        val sakstema = Sakstema.fromExternal(it.tema!!)

        Journalpost(
            journalpostId = it.journalpostId,
            tittel = it.tittel ?: "---",
            temakode = sakstema.name,
            temanavn = sakstema.navn,
            avsender = mapAvsender(it.avsender, it.mottaker, it.journalposttype),
            mottaker = mapMottaker(it.mottaker, it.avsender, it.journalposttype),
            journalposttype = mapJournalpostType(it.journalposttype),
            opprettet = sorteringsdato(it.datoSortering),
            sorteringsdato = sorteringsdato(it.datoSortering),
            dokument = dokument,
            vedlegg = vedlegg
        )
    }
}

private fun mapJournalpostType(type: Journalposttype) = when(type) {
    Journalposttype.I -> "Inn"
    Journalposttype.U -> "Ut"
    else -> "Notat"
}

private fun mapAvsender(avsender: AvsenderMottaker?, mottaker: AvsenderMottaker?, type: Journalposttype): String? {
    return when {
        avsender == null && mottaker == null && type == Journalposttype.U -> "NAV"
        avsender != null -> avsender.navn
        else -> null
    }
}

private fun mapMottaker(mottaker: AvsenderMottaker?, avsender: AvsenderMottaker?, type: Journalposttype): String? {
    return when {
        mottaker == null && avsender == null && type == Journalposttype.I -> "NAV"
        mottaker != null -> mottaker.navn
        else -> null
    }
}

private val log = KotlinLogging.logger {}

private fun dokumenter(dokumenter: List<DokumentInfo?>?): List<DokumentHeader> {

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
                    DokumentHeader(
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
            listOf(DokumentHeader.blank())
        }
}

private fun sorteringsdato(datoSortering: String): ZonedDateTime {
    return ZonedDateTime.parse("${datoSortering}Z")
}
