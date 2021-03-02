package no.nav.personbruker.minesaker.api.saf.domain

data class Dokumentinfo(
    val tittel: Tittel,
    val filuuid: FilUUID,
    val brukerHarTilgang: Boolean
)
