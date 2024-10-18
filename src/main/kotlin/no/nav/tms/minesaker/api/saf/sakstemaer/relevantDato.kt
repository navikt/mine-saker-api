package no.nav.tms.minesaker.api.saf.sakstemaer

import java.time.ZonedDateTime

fun List<SafRelevantDato?>.toInternal(): ZonedDateTime {
    val internal = filterNotNull().map { external -> external.toInternal() }
    return internal.finnSistEndret()
}

fun List<ZonedDateTime>.finnSistEndret(): ZonedDateTime {
    return maxByOrNull { it }
        ?: throw SakstemaException("Ingen datoer å sammenligne, listen er tom.", SakstemaException.ErrorType.MISSING_FIELD)
}


fun SafRelevantDato.toInternal(): ZonedDateTime = ZonedDateTime.parse("${dato}Z")
