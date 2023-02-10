package no.nav.personbruker.minesaker.api.domain

import java.net.URL
import java.time.ZonedDateTime

data class ForenkletSakstema(
    val navn: String,
    val kode: Sakstemakode,
    val sistEndret: ZonedDateTime?,
    val detaljvisningUrl : URL
)
