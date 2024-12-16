package no.nav.tms.minesaker.api.saf.fullmakt

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.receive
import no.nav.tms.minesaker.api.idportenUser

fun Route.fullmaktApi(fullmaktService: FullmaktService, sessionStore: FullmaktSessionStore) {

    enableFullmakt {

        get("/fullmakt/info") {
            val fullmaktGiver = fullmaktGiver

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
    }

    get("/fullmakt/forhold") {
        call.respond(fullmaktService.getFullmaktForhold(idportenUser))
    }

    post("/fullmakt/representert") {
        val representert = call.represertIdent()

        if (representert == idportenUser.ident) {
            sessionStore.clearFullmaktGiver(idportenUser.ident)
            call.respond(HttpStatusCode.OK)
        } else {
            val validForhold = fullmaktService.validateFullmaktsGiver(idportenUser, representert)

            sessionStore.setFullmaktGiver(idportenUser.ident, validForhold)

            call.respond(HttpStatusCode.OK)
        }
    }
}

private val RoutingContext.fullmaktGiver get() =
    call.attributes.getOrNull(FullmaktAttribute)

private suspend fun ApplicationCall.represertIdent() = receive<Representert>().ident

private data class Representert(
    val ident: String
)

data class FullmaktInfo(
    val viserRepresentertesData: Boolean,
    val representertNavn: String? = null,
    val representertIdent: String? = null,
)
