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
    val avsender: String?,
    val mottaker: String?,
    val opprettet: ZonedDateTime,
    val dokument: DokumentHeaderV2,
    val vedlegg: List<DokumentHeaderV2>
)

data class DokumentHeaderV2(
    val dokumentInfoId: String,
    val tittel: String,
    val filtype: String,
    val filstorrelse: Int,
    val brukerHarTilgang: Boolean,
    val sladdet: Boolean
) {
    companion object {
        fun blank() = DokumentHeaderV2(
            dokumentInfoId = "",
            tittel = "",
            filtype = "",
            filstorrelse = 0,
            brukerHarTilgang = true,
            sladdet = false,
        )
    }
}

typealias SafJournalposttypeV2 = no.nav.dokument.saf.selvbetjening.generated.dto.enums.Journalposttype
