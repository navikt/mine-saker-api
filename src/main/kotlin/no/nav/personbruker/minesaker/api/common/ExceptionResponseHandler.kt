package no.nav.personbruker.minesaker.api.common

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import no.nav.personbruker.minesaker.api.common.exception.GraphQLResultException
import no.nav.personbruker.minesaker.api.common.exception.InvalidRequestException
import no.nav.personbruker.minesaker.api.common.exception.SafException
import org.slf4j.Logger

suspend fun ApplicationCall.respondWithError(log: Logger, exception: Exception) {
    when (exception) {
        is InvalidRequestException -> {
            respond(HttpStatusCode.BadRequest)
            val msg = "Mottok en request med feil input. context=${exception.context}"
            log.warn(msg, exception)
        }
        is GraphQLResultException -> {
            respond(HttpStatusCode.ServiceUnavailable)
            val msg = "Klarte ikke å hente data fra SAF. Returnerer feilkode til frontend. context=${exception.context}"
            log.warn(msg, exception)
        }
        is SafException -> {
            respond(HttpStatusCode.ServiceUnavailable)
            val msg = "Klarte ikke å hente data fra SAF. Returnerer feilkode til frontend. context=${exception.context}"
            log.warn(msg, exception)
        }
        else -> {
            respond(HttpStatusCode.InternalServerError)
            log.error("Ukjent feil oppstod ved henting av eventer. Returnerer feilkode til frontend", exception)
        }
    }
}
