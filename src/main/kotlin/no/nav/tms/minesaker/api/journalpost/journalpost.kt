package no.nav.tms.minesaker.api.journalpost

import java.time.ZonedDateTime

data class Journalpost(
    val journalpostId: String,
    val tittel: String,
    val temakode: String,
    val temanavn: String,
    val avsender: String?,
    val mottaker: String?,
    val journalposttype: String,
    val opprettet: ZonedDateTime,
    val dokument: DokumentHeader,
    val vedlegg: List<DokumentHeader>
)

data class ForenkletJournalpost(
    val journalpostId: String,
    val tittel: String,
    val temakode: String,
    val avsender: String?,
    val mottaker: String?,
    val opprettet: ZonedDateTime,
    val dokumentInfoId: String?
)

data class DokumentHeader(
    val dokumentInfoId: String,
    val tittel: String,
    val filtype: String,
    val filstorrelse: Int,
    val brukerHarTilgang: Boolean,
    val tilgangssperre: Tilgangssperre?
) {
    companion object {
        fun blank() = DokumentHeader(
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
