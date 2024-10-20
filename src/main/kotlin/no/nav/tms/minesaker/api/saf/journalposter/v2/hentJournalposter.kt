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

fun HentJournalposterV2.Result.toInternal(): HentJournalposterResponseV2? {

    return dokumentoversiktSelvbetjening.tema.firstOrNull()?.let { tema ->

        val journalposter = tema.journalposter.filterNotNull().map {
            val (dokument, vedlegg) = dokumenter(it.dokumenter).let { dokumenter ->
                dokumenter.first() to dokumenter.drop(1)
            }

            JournalpostV2(
                journalpostId = it.journalpostId,
                tittel = it.tittel ?: "---",
                avsender = mapAvsender(it.avsender, it.mottaker, it.journalposttype),
                mottaker = mapMottaker(it.mottaker, it.avsender, it.journalposttype),
                opprettet = opprettet(it.relevanteDatoer),
                dokument = dokument,
                vedlegg = vedlegg
            )
        }

        HentJournalposterResponseV2(
            tema.kode,
            tema.navn,
            journalposter
        )
    }
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
                        sladdet = variant.variantformat == Variantformat.SLADDET
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
