package no.nav.personbruker.minesaker.api.common

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import no.nav.personbruker.minesaker.api.common.exception.SafException
import org.slf4j.Logger
import java.lang.Exception

suspend fun respondWithError(call: ApplicationCall, log: Logger, exception: Exception) {
    when(exception) {
        is SafException -> {
            call.respond(HttpStatusCode.ServiceUnavailable)
            log.warn("Klarte ikke å hente data fra SAF. Returnerer feilkode til frontend. context={}", exception.context, exception)
        }
        else -> {
            call.respond(HttpStatusCode.InternalServerError)
            log.error("Ukjent feil oppstod ved henting av eventer. Returnerer feilkode til frontend", exception)
        }
    }
}
