package no.nav.tms.minesaker.api.saf.journalposter.v2

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.ZonedDateTime

data class HentJournalposterResponseV2(
    val kode: String,
    val navn: String,

    val journalposter: List<JournalpostV2>
)

data class JournalpostV2(
    val journalpostId: String,
    val tittel: String,
    val journalposttype: JournalposttypeV2,
    val journalstatus: String,
    @JsonIgnore val avsender: AvsenderMottakerV2?,
    @JsonIgnore val mottaker: AvsenderMottakerV2?,
    val opprettet: ZonedDateTime,
    val dokumenter: List<DokumentHeaderV2>,
    @JsonIgnore val sakstema: Sakstema? = null
) {
    val temakode = sakstema?.name
    val temanavn = sakstema?.navn
    val avsendertype = avsender?.type
    val avsendernavn = avsender?.navn
    val mottakertype = mottaker?.type
    val mottakernavn = mottaker?.navn
}

data class DokumentHeaderV2(
    val dokumentInfoId: String,
    val tittel: String,
    val dokumenttype: DokumenttypeV2,
    val filtype: String,
    val filstorrelse: Int,
    val brukerHarTilgang: Boolean,
    val sladdet: Boolean
)

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
