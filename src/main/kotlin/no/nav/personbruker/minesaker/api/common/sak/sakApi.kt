package no.nav.personbruker.minesaker.api.common.sak

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import no.nav.personbruker.minesaker.api.common.exception.InvalidRequestException
import no.nav.personbruker.minesaker.api.common.respondWithError
import no.nav.personbruker.minesaker.api.config.idportenUser
import no.nav.personbruker.minesaker.api.saf.domain.Sakstemakode
import no.nav.personbruker.minesaker.api.saf.domain.toInternalSaktemakode
import org.slf4j.LoggerFactory

fun Route.sakApi(
    service: SakService
) {

    val log = LoggerFactory.getLogger(SakService::class.java)

    get("/journalposter") {
        try {
            val sakstema = extractOnsketSakstema()
            val result = service.hentJournalposterForSakstema(idportenUser, sakstema)
            call.respond(HttpStatusCode.OK, result)

        } catch (exception: Exception) {
            call.respondWithError(log, exception)
        }
    }

    get("/sakstemaer") {
        try {
            val result = service.hentSakstemaer(idportenUser)
            call.respond(HttpStatusCode.OK, result)

        } catch (exception: Exception) {
            call.respondWithError(log, exception)
        }
    }

    get("/triggfeil") {
        try {
            val result = service.hentSakstemaerTriggFeil(idportenUser)
            call.respond(HttpStatusCode.OK, result)

        } catch (exception: Exception) {
            call.respondWithError(log, exception)
        }
    }

}

private fun PipelineContext<Unit, ApplicationCall>.extractOnsketSakstema(): Sakstemakode {
    val sakstemakode: String = call.request.queryParameters["sakstemakode"]
            ?: throw InvalidRequestException("Kallet kan ikke utføres uten at tema er valgt.")

    val sakstema = runCatching {
        sakstemakode.toInternalSaktemakode()

    }.onFailure { cause ->
        throw InvalidRequestException("Ugyldig sakstemakode ble brukt", cause)
    }

    return sakstema.getOrThrow()
}
