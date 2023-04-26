package no.nav.personbruker.minesaker.api.domain

import java.time.ZonedDateTime

data class Sakstema(
    val navn: String,
    val kode: Sakstemakode,
    val journalposter: List<Journalpost> = emptyList()
)

data class SistEndredeSakstemaer(
    val sistEndrede: List<ForenkletSakstema>,
    val dagpengerSistEndret: ZonedDateTime?
)


//midlertidige mapper-klasse slik att dittnav-api fortsetter å fungere
data class InternalSakstemaResponse(
    val sakerURL: String,
    val sakstemaer: List<ForenkletSakstema>,
    val dagpengerSistEndret: ZonedDateTime?)

data class ForenkletSakstema(
    val navn: String,
    val kode: Sakstemakode,
    val sistEndret: ZonedDateTime?,
    val detaljvisningUrl : String
)

