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
        val fullmaktGiver = call.fullmaktAttribute?.fullmaktGiver

        if (fullmaktGiver == null) {
            call.respond(FullmaktInfo(false))
        } else {
            call.respond(
                FullmaktInfo(
                    viserRepresentertesData = true,
                    representertNavn = fullmaktGiver.navn,
                    representertIdent = fullmaktGiver.ident
                )
            )
        }
    }

    get("/fullmakt/forhold") {
        call.respond(fullmaktService.getFullmaktForhold(idportenUser))
    }

    post("/fullmakt/representert") {
        val representert = call.represertIdent()

        if (representert == idportenUser.ident) {
            redisService.clearFullmaktGiver(idportenUser.ident)
            call.respond(HttpStatusCode.OK)
        } else {
            val validForhold = fullmaktService.validateFullmaktsGiver(idportenUser, representert)

            redisService.setFullmaktGiver(idportenUser.ident, validForhold)

            call.respond(HttpStatusCode.OK)
        }
    }
}

private val ApplicationCall.fullmaktAttribute get() =
    attributes.getOrNull(FullmaktInterception.FullmaktAttribute)

private suspend fun ApplicationCall.represertIdent() = receive<Representert>().ident

private data class Representert(
    val ident: String
)

private data class FullmaktInfo(
    val viserRepresentertesData: Boolean,
    val representertNavn: String? = null,
    val representertIdent: String? = null,
)
