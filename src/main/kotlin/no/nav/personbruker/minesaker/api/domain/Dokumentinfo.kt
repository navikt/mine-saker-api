package no.nav.personbruker.minesaker.api.domain

data class Dokumentinfo(
    val tittel: String,
    val dokumentInfoId: String,
    val dokumenttype : Dokumenttype,
    val brukerHarTilgang: Boolean,
    val eventuelleGrunnerTilManglendeTilgang : List<String>,
    val variant : Dokumentvariant
)
