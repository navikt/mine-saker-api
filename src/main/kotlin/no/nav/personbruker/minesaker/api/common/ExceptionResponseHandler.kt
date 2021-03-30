package no.nav.personbruker.minesaker.api.common

import io.ktor.http.*
import no.nav.personbruker.minesaker.api.common.exception.*
import org.slf4j.Logger

object ExceptionResponseHandler {
    fun logExceptionAndDecideErrorResponseCode(log: Logger, exception: Exception): HttpStatusCode {
        return when (exception) {
            is DocumentNotFoundException -> {
                val errorCode = HttpStatusCode.NotFound
                val msg = "Dokumentet ble ikke funnet. Returnerer feilkoden $errorCode. $exception"
                log.warn(msg, exception)
                errorCode
            }
            is GraphQLResultException -> {
                val errorCode = HttpStatusCode.ServiceUnavailable
                val msg = "Det skjedde en graphQL-feil. Returnerer feilkoden $errorCode. $exception"
                log.warn(msg, exception)
                errorCode
            }
            is InvalidRequestException -> {
                val errorCode = HttpStatusCode.BadRequest
                val msg = "Mottok en request med feil input. Returnerer feilkoden $errorCode. $exception"
                log.warn(msg, exception)
                errorCode
            }
            is MissingFieldException -> {
                val errorCode = HttpStatusCode.ServiceUnavailable
                val msg = "Klarte ikke å transformere til intern-modell, grunnet manglende data. Returnerer " +
                        "feilkoden $errorCode. $exception"
                log.warn(msg, exception)
                errorCode
            }
            is CommunicationException -> {
                val errorCode = HttpStatusCode.ServiceUnavailable
                val msg = "Klarte ikke å hente data. Returnerer feilkoden $errorCode. $exception"
                log.warn(msg, exception)
                errorCode
            }
            is UgyldigVerdiException -> {
                val errorCode = HttpStatusCode.InternalServerError
                val msg = "Det skjedde en feil ved konvertering til den interne-modellen. Returnerer " +
                        "feilkoden $errorCode. $exception"
                log.warn(msg, exception)
                errorCode
            }
            is UnknownValueException -> {
                val errorCode = HttpStatusCode.InternalServerError
                val msg = "Klarte ikke å transformere til intern-modell, grunnet ukjent verdi mottatt. Kan " +
                        "GraphQL-schema-et ha endret seg? Returnerer feilkoden $errorCode. $exception"
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
