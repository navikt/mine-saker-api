package no.nav.personbruker.minesaker.api.saf.domain

data class Dokumentinfo(
    val tittel: String,
    val filuuid: String,
    val brukerHarTilgang: Boolean
)
