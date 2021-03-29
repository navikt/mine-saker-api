package no.nav.personbruker.minesaker.api.common

import io.ktor.http.*
import no.nav.personbruker.minesaker.api.common.exception.GraphQLResultException
import no.nav.personbruker.minesaker.api.common.exception.InvalidRequestException
import no.nav.personbruker.minesaker.api.common.exception.SafException
import no.nav.personbruker.minesaker.api.common.exception.UgyldigVerdiException
import org.slf4j.Logger

object ExceptionResponseHandler {
    fun logExceptionAndDecideErrorResponseCode(log: Logger, exception: Exception): HttpStatusCode {
        return when (exception) {
            is InvalidRequestException -> {
                val errorCode = HttpStatusCode.BadRequest
                val msg = "Mottok en request med feil input. Returnerer feilkoden $errorCode. $exception"
                log.warn(msg, exception)
                errorCode
            }
            is GraphQLResultException -> {
                val errorCode = HttpStatusCode.ServiceUnavailable
                val msg = "Det skjedde en graphQL-feil. Returnerer feilkoden $errorCode. $exception"
                log.warn(msg, exception)
                errorCode
            }
            is SafException -> {
                val errorCode = HttpStatusCode.ServiceUnavailable
                val msg = "Klarte ikke å hente data. Returnerer feilkoden $errorCode. $exception"
                log.warn(msg, exception)
                errorCode
            }
            is UgyldigVerdiException -> {
                val errorCode = HttpStatusCode.InternalServerError
                val msg = "Det skjedde en feil ved konvertering til den interne-modellen. Returnerer feilkoden $errorCode. $exception"
                log.warn(msg, exception)
                errorCode
            }
            else -> {
                val errorCode = HttpStatusCode.InternalServerError
                val msg = "Ukjent feil oppstod. Returnerer feilkoden $errorCode. $exception"
                log.error(msg, exception)
                errorCode
            }
        }
    }
}
