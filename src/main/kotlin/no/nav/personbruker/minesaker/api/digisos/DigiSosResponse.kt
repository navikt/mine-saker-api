package no.nav.personbruker.minesaker.api.digisos

import no.nav.personbruker.minesaker.api.config.innsynsUrlResolverSingleton
import no.nav.personbruker.minesaker.api.domain.ForenkletSakstema
import no.nav.personbruker.minesaker.api.domain.Navn
import no.nav.personbruker.minesaker.api.domain.Sakstemakode
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

data class DigiSosResponse(
    val navn : String,
    val kode : String,
    val sistEndret : LocalDateTime
)

fun DigiSosResponse.toInternal(): ForenkletSakstema {
    val sakstemakode = Sakstemakode.valueOf(kode)
    return ForenkletSakstema(
        Navn(navn),
        sakstemakode,
        sistEndret.toZonedDateTimeUTC(),
        innsynsUrlResolverSingleton.urlFor(sakstemakode)
    )
}

fun List<DigiSosResponse>.toInternal() : List<ForenkletSakstema> {
    return map { result ->
        result.toInternal()
    }
}

private fun LocalDateTime.toZonedDateTimeUTC(): ZonedDateTime? {
    return ZonedDateTime.of(this, ZoneId.of("UTC"))
}

