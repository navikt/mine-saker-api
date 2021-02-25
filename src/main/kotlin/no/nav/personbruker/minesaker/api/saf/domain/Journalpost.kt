package no.nav.personbruker.minesaker.api.saf.domain

import java.time.ZonedDateTime

data class Journalpost(
    val tittel: String,
    val journalpostId: String,
    val journalposttype: Journalposttype,
    val avsenderMottaker: AvsenderMottaker,
    val datoRegistert: ZonedDateTime,
    val arkiverteDokumenter: List<Dokumentinfo> = emptyList()
)
