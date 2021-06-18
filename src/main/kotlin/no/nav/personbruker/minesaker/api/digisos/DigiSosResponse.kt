package no.nav.personbruker.minesaker.api.digisos

import java.time.ZonedDateTime

data class DigiSosResponse(
    val navn : String,
    val kode : String,
    val sistEndret : ZonedDateTime
)
