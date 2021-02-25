package no.nav.personbruker.minesaker.api.saf.domain

data class Journalpost(
    val tittel: String,
    val journalpostId: String,
    val journalposttype: Journalposttype,
    val avsenderMottaker: AvsenderMottaker,
    val relevanteDatoer: List<RelevantDato>,
    val arkiverteDokumenter: List<Dokumentinfo> = emptyList()
)
