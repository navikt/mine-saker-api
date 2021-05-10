package no.nav.personbruker.minesaker.api.domain

import java.time.ZonedDateTime

data class Journalpost(
    val tittel: Tittel,
    val journalpostId: JournalpostId,
    val journalposttype: Journalposttype,
    val avsenderMottaker: AvsenderMottaker,
    val sisteEndret: ZonedDateTime,
    val arkiverteDokumenter: List<Dokumentinfo> = emptyList()
)
