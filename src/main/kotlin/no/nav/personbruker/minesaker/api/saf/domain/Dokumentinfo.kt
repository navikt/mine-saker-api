package no.nav.personbruker.minesaker.api.saf.domain

data class Dokumentinfo(
    val tittel: Tittel,
    val dokumentInfoId: DokumentInfoId,
    val dokumenttype : Dokumenttype,
    val brukerHarTilgang: Boolean,
    val eventuelleGrunnerTilManglendeTilgang : List<String>
)
