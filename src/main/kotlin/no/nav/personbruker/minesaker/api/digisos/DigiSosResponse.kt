package no.nav.personbruker.minesaker.api.digisos

import no.nav.personbruker.minesaker.api.config.InnsynsUrlResolver
import no.nav.personbruker.minesaker.api.domain.ForenkletSakstema
import no.nav.personbruker.minesaker.api.domain.Sakstemakode
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime


data class DigiSosResponse(
    val navn : String,
    val kode : String,
    val sistEndret : LocalDateTime
)

fun DigiSosResponse.toInternal(innsynsUrlResolver:InnsynsUrlResolver): ForenkletSakstema {
    val sakstemakode = Sakstemakode.valueOf(kode)
    return ForenkletSakstema(
        navn,
        sakstemakode,
        sistEndret.toZonedDateTimeUTC(),
        innsynsUrlResolver.urlFor(sakstemakode)
    )
}

fun List<DigiSosResponse>.toInternal(innsynsUrlResolver:InnsynsUrlResolver) : List<ForenkletSakstema> {
    return map { result ->
        result.toInternal(innsynsUrlResolver)
    }
}

private fun LocalDateTime.toZonedDateTimeUTC(): ZonedDateTime? {
    return ZonedDateTime.of(this, ZoneId.of("UTC"))
}

