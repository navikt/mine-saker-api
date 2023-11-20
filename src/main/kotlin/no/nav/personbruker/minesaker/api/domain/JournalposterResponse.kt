package no.nav.personbruker.minesaker.api.domain

import java.time.ZonedDateTime

data class JournalposterResponse(
    val temanavn: String,
    val temakode: Sakstemakode,
    val journalposter: List<Journalpost> = emptyList()
) {
    val navn get() = temanavn
    val kode get() = temakode
}

data class SistEndredeSakstemaer(
    val sistEndrede: List<ForenkletSakstema>,
    val dagpengerSistEndret: ZonedDateTime?
)

data class SakstemaerResponse(
    val sakerURL: String,
    val sakstemaer: List<ForenkletSakstema>,
    val dagpengerSistEndret: ZonedDateTime?)

data class ForenkletSakstema(
    val navn: String,
    val kode: Sakstemakode,
    val sistEndret: ZonedDateTime?,
    val detaljvisningUrl : String
)

