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

data class AvsenderMottakerV2(
    val type: AvsenderMottakerTypeV2,
    val navn: String
)

enum class AvsenderMottakerTypeV2 {
    NAV, Bruker, Person, Organisasjon, Helsepersonell, Internasjonal, Null, Ukjent
}

enum class JournalposttypeV2 {
    Inn, Ut, Notat
}

enum class DokumenttypeV2 {
    Hoved, Vedlegg
}

typealias SafJournalposttypeV2 = no.nav.dokument.saf.selvbetjening.generated.dto.enums.Journalposttype
typealias SafJournalstatusV2 = no.nav.dokument.saf.selvbetjening.generated.dto.enums.Journalstatus

class DecayingToggle<T>(private val initial: T, private val fallback: T) {
    private var isDecayed = false

    val value: T get() =
        if (!isDecayed) {
            isDecayed = true
            initial
        } else {
            fallback
        }
}
