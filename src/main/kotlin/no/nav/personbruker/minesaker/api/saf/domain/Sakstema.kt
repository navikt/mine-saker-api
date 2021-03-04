package no.nav.personbruker.minesaker.api.saf.domain

data class Sakstema(
    val navn: Navn,
    val kode: String,
    val journalposter : List<Journalpost> = emptyList()
)
