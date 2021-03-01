package no.nav.personbruker.minesaker.api.saf.domain

data class Sakstema(
    val navn: String,
    val kode: String,
    val journalposter : List<Journalpost> = emptyList()
)
