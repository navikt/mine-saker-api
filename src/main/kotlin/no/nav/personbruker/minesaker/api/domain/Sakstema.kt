package no.nav.personbruker.minesaker.api.domain

data class Sakstema(
    val navn: Navn,
    val kode: Sakstemakode,
    val journalposter : List<Journalpost> = emptyList()
)
