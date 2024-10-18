package no.nav.tms.minesaker.api.digisos

import no.nav.tms.minesaker.api.saf.InnsynsUrlResolver
import no.nav.tms.minesaker.api.saf.sakstemaer.ForenkletSakstema
import no.nav.tms.minesaker.api.saf.sakstemaer.Sakstemakode
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime


data class DigiSosResponse(
    val navn : String,
    val kode : String,
    val sistEndret : LocalDateTime
)

fun DigiSosResponse.toInternal(innsynsUrlResolver: InnsynsUrlResolver): ForenkletSakstema {
    val sakstemakode = Sakstemakode.valueOf(kode)
    return ForenkletSakstema(
        navn,
        sakstemakode,
        sistEndret.toZonedDateTimeUTC(),
        innsynsUrlResolver.urlFor(sakstemakode)
    )
}

fun List<DigiSosResponse>.toInternal(innsynsUrlResolver: InnsynsUrlResolver) : List<ForenkletSakstema> {
    return map { result ->
        result.toInternal(innsynsUrlResolver)
    }
}

private fun LocalDateTime.toZonedDateTimeUTC(): ZonedDateTime? {
    return ZonedDateTime.of(this, ZoneId.of("UTC"))
}

