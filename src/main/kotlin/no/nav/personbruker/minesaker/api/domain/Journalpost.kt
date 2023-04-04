package no.nav.personbruker.minesaker.api.domain

import java.time.ZonedDateTime

data class Journalpost(
    val tittel: String,
    val journalpostId: String,
    val journalposttype: Journalposttype,
    val avsender: Dokumentkilde?,
    val mottaker: Dokumentkilde?,
    val sisteEndret: ZonedDateTime,
    val dokumenter: List<Dokumentinfo> = emptyList(),
    val harVedlegg: Boolean = dokumenter.size > 1
)

enum class Journalposttype {
    INNGAAENDE,
    UTGAAENDE,
    NOTAT
}
