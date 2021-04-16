package no.nav.personbruker.minesaker.api.saf.domain

import java.time.ZonedDateTime

data class ForenkletSakstema(
    val navn: Navn,
    val kode: Sakstemakode,
    val sistEndret: ZonedDateTime?
)
