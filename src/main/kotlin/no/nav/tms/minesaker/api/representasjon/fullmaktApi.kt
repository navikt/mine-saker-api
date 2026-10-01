package no.nav.tms.minesaker.api.representasjon

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.receive
import no.nav.tms.minesaker.api.user

fun Route.reprApi(reprService: ReprService, sessionStore: ReprSessionStore) {

    enableRepresentasjon {

        get("/fullmakt/info") {
            val fullmaktGiver = fullmaktGiver

            if (fullmaktGiver == null) {
                call.respond(Representasjonsinfo(false))
            } else {
                call.respond(
                    Representasjonsinfo(
                        viserRepresentertesData = true,
                        representertNavn = fullmaktGiver.navn,
                        representertIdent = fullmaktGiver.ident
                    )
                )
            }
        }

        get("/repr/info") {
            val fullmaktGiver = fullmaktGiver

            if (fullmaktGiver == null) {
                call.respond(Representasjonsinfo(false))
            } else {
                call.respond(
                    Representasjonsinfo(
                        viserRepresentertesData = true,
                        representertNavn = fullmaktGiver.navn,
                        representertIdent = fullmaktGiver.ident
                    )
                )
            }
        }
    }

    get("/fullmakt/forhold") {
        call.respond(reprService.getFullmaktForhold(call.user))
    }

    get("/repr/forhold") {
        call.respond(reprService.getFullmaktForhold(call.user))
    }

    post("/fullmakt/representert") {
        val representert = call.representert()

        val user = call.user

        if (representert.ident == user.ident) {
            sessionStore.clearRepresentert(user.ident)
            call.respond(HttpStatusCode.OK)
        } else {
            val validForhold = reprService.validateForhold(user, representert.ident, representert.type)

            sessionStore.setRepresentert(user.ident, validForhold)

            call.respond(HttpStatusCode.OK)
        }
    }

    post("/repr/representert") {
        val representert = call.representert()

        val user = call.user

        if (representert.ident == user.ident) {
            sessionStore.clearRepresentert(user.ident)
            call.respond(HttpStatusCode.OK)
        } else {
            val validForhold = reprService.validateForhold(user, representert.ident, representert.type)

            sessionStore.setRepresentert(user.ident, validForhold)

            call.respond(HttpStatusCode.OK)
        }
    }
}

private val RoutingContext.fullmaktGiver get() =
    call.attributes.getOrNull(FullmaktAttribute)

private suspend fun ApplicationCall.representert() = receive<RepresentertRequest>()

private data class RepresentertRequest(
    val ident: String,
    val type: Representasjonstype = Representasjonstype.Fullmakt
)

data class Representasjonsinfo(
    val viserRepresentertesData: Boolean,
    val forsholdstype: Representasjonstype? = null,
    val representertNavn: String? = null,
    val representertIdent: String? = null,
)
