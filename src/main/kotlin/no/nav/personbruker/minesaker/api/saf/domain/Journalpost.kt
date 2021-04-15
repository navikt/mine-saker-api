package no.nav.personbruker.minesaker.api.saf.domain

data class Journalpost(
    val tittel: Tittel,
    val journalpostId: JournalpostId,
    val journalposttype: Journalposttype,
    val avsenderMottaker: AvsenderMottaker,
    val sisteEndret: RelevantDato,
    val arkiverteDokumenter: List<Dokumentinfo> = emptyList()
)
