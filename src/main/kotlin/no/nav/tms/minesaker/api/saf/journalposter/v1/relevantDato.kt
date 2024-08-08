package no.nav.tms.minesaker.api.saf.journalposter.v1

import no.nav.tms.minesaker.api.exception.TransformationException
import java.time.ZonedDateTime

fun List<SafRelevantDato?>.toInternal(): ZonedDateTime {
    val internal = filterNotNull().map { external -> external.toInternal() }
    return internal.finnSistEndret()
}

fun List<ZonedDateTime>.finnSistEndret(): ZonedDateTime {
    return maxByOrNull { it }
        ?: throw TransformationException("Ingen datoer å sammenligne, listen er tom.", TransformationException.ErrorType.MISSING_FIELD)
}


fun SafRelevantDato.toInternal(): ZonedDateTime = ZonedDateTime.parse("${dato}Z")
