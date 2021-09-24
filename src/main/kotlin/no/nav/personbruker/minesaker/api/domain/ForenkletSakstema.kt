package no.nav.personbruker.minesaker.api.domain

import java.net.URL
import java.time.ZonedDateTime

data class ForenkletSakstema(
    val navn: Navn,
    val kode: Sakstemakode,
    val sistEndret: ZonedDateTime?,
    val detaljvisningUrl : URL
)
