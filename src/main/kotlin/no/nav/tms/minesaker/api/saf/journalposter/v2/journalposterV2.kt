package no.nav.tms.minesaker.api.saf.journalposter.v2

import java.time.ZonedDateTime

data class HentJournalposterResponseV2(
    val kode: String,
    val navn: String,

    val journalposter: List<JournalpostV2>
)

data class JournalpostV2(
    val journalpostId: String,
    val tittel: String,
    val temakode: String,
    val temanavn: String,
    val avsender: String?,
    val mottaker: String?,
    val journalposttype: String,
    val opprettet: ZonedDateTime,
    val dokument: DokumentHeaderV2,
    val vedlegg: List<DokumentHeaderV2>
)

data class ForenkletJournalpostV2(
    val journalpostId: String,
    val tittel: String,
    val temakode: String,
    val avsender: String?,
    val mottaker: String?,
    val opprettet: ZonedDateTime,
    val dokumentInfoId: String?
)

data class DokumentHeaderV2(
    val dokumentInfoId: String,
    val tittel: String,
    val filtype: String,
    val filstorrelse: Int,
    val brukerHarTilgang: Boolean,
    val tilgangssperre: Tilgangssperre?
) {
    companion object {
        fun blank() = DokumentHeaderV2(
            dokumentInfoId = "",
            tittel = "",
            filtype = "",
            filstorrelse = 0,
            brukerHarTilgang = true,
            tilgangssperre = null
        )
    }
}

enum class Tilgangssperre {
    Tredjepart, SkannetDokument, Annet;

    companion object {
        fun parse(kode: String) = when(kode.lowercase()) {
            "annen_part" -> Tredjepart
            "skannet_dokument" -> SkannetDokument
            else -> Annet
        }
    }
}

typealias SafJournalposttypeV2 = no.nav.dokument.saf.selvbetjening.generated.dto.enums.Journalposttype
