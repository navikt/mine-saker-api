package no.nav.personbruker.minesaker.api.common.sak

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.personbruker.minesaker.api.common.respondWithError
import no.nav.personbruker.minesaker.api.config.authenticatedUser
import org.slf4j.LoggerFactory

fun Route.sakApi(
    service: SakService
) {

    val log = LoggerFactory.getLogger(SakService::class.java)

    get("/journalposter") {
        try {
            val sakstemakode: String = call.request.queryParameters["sakstemakode"]
                ?: throw RuntimeException("Kallet kan ikke utføres uten at tema er valgt.")

            val result = service.hentJournalposterForSakstema(authenticatedUser, sakstemakode)
            call.respond(HttpStatusCode.OK, result)

        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/sakstemaer") {
        try {
            val result = service.hentSakstemaer(authenticatedUser)
            call.respond(HttpStatusCode.OK, result)

        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

}
