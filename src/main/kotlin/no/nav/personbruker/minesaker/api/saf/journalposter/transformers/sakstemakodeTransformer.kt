package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.personbruker.minesaker.api.exception.TransformationException
import no.nav.personbruker.minesaker.api.domain.Sakstemakode

fun String.toInternalSaktemakode(): Sakstemakode {
    val gyldigSakstemakode = runCatching {
        Sakstemakode.valueOf(this)

    }.onFailure { cause ->
        throw TransformationException("Ukjent sakstemakode", TransformationException.ErrorType.UNKNOWN_VALUE, cause)
            .addContext("ukjentVerdi", this)
    }

    return gyldigSakstemakode.getOrThrow()
}
