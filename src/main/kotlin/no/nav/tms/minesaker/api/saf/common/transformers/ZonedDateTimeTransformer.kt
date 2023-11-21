package no.nav.tms.minesaker.api.saf.common.transformers

import no.nav.tms.minesaker.api.exception.TransformationException
import no.nav.tms.minesaker.api.exception.TransformationException.ErrorType.MISSING_FIELD
import java.time.ZonedDateTime

fun List<ZonedDateTime>.finnSistEndret(): ZonedDateTime {
    return maxByOrNull { it }
        ?: throw TransformationException("Ingen datoer å sammenligne, listen er tom.", MISSING_FIELD)
}
