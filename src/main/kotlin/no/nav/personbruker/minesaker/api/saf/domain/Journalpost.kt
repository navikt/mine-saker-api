package no.nav.personbruker.minesaker.api.saf.domain

import java.time.ZonedDateTime

data class Journalpost(
    val tittel: Tittel,
    val journalpostId: JournalpostId,
    val journalposttype: Journalposttype,
    val avsenderMottaker: AvsenderMottaker,
    val sisteEndret: ZonedDateTime,
    val dokumenter: List<Dokumentinfo> = emptyList()
)
