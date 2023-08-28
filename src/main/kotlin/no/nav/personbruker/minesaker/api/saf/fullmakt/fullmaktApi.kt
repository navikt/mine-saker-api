package no.nav.personbruker.minesaker.api.saf.fullmakt

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.receive
import no.nav.personbruker.minesaker.api.config.idportenUser
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser

fun Route.fullmaktApi(fullmaktService: FullmaktService, redisService: FullmaktRedisService) {
    get("/fullmakt/info") {
        val fullmaktSession = call.fullmaktAttribute?.fullmakt

        if (fullmaktSession == null) {
            call.respond(FullmaktInfo(false))
        } else {
            call.respond(FullmaktInfo(true, fullmaktSession.representertNavn))
        }
    }

    get("/fullmakt/forhold") {
        call.respond(fullmaktService.getFullmaktForhold(idportenUser))
    }

    post("/fullmakt/representert") {
        val representert = call.represertIdent()

        if (representert == idportenUser.ident) {
            redisService.clearForhold(idportenUser.subject)
            call.respond(HttpStatusCode.OK)
        } else {
            val validForhold = fullmaktService.validateFullmaktsForhold(idportenUser, representert)

            redisService.setForhold(idportenUser.subject, validForhold)

            call.respond(HttpStatusCode.OK)
        }
    }
}

private val ApplicationCall.fullmaktAttribute get() =
    attributes.getOrNull(FullmaktInterception.FullmaktAttribute)

private val IdportenUser.subject get() = jwt.subject

private suspend fun ApplicationCall.represertIdent() = receive<Representert>().ident

private data class Representert(
    val ident: String
)

private data class FullmaktInfo(
    val viserRepresentertesData: Boolean,
    val representertNavn: String? = null
)
