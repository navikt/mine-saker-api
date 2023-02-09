package no.nav.personbruker.minesaker.api.domain

data class Sakstema(
    val navn: String,
    val kode: Sakstemakode,
    val journalposter : List<Journalpost> = emptyList()
)
