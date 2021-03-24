package no.nav.personbruker.minesaker.api.saf.domain

data class Dokumentinfo(
    val tittel: Tittel,
    val dokumentInfoId: DokumentInfoId,
    val brukerHarTilgang: Boolean
)
