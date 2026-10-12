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
            val representert = this.representert

            if (representert == null) {
                call.respond(Representasjonsinfo.viserEgne())
            } else {
                call.respond(
                    Representasjonsinfo.viserRepresentert(representert)
                )
            }
        }

        get("/repr/info") {
            val representert = this.representert

            if (representert == null) {
                call.respond(Representasjonsinfo.viserEgne())
            } else {
                call.respond(
                    Representasjonsinfo.viserRepresentert(representert)
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

private val RoutingContext.representert get() =
    call.attributes.getOrNull(RepresentertAttribute)

private suspend fun ApplicationCall.representert() = receive<RepresentertRequest>()

private data class RepresentertRequest(
    val ident: String,
    val type: Representasjonstype = Representasjonstype.Fullmakt
)

private data class Representasjonsinfo(
    val viserRepresentertesData: Boolean,
    val forsholdstype: Representasjonstype?,
    val representertNavn: String?,
    val representertIdent: String?,
) {
    companion object {
        fun viserEgne() = Representasjonsinfo(
            viserRepresentertesData = false,
            forsholdstype = null,
            representertNavn = null,
            representertIdent = null,
        )

        fun viserRepresentert(representert: Representert) = Representasjonsinfo(
            viserRepresentertesData = true,
            forsholdstype = representert.type,
            representertNavn = representert.navn,
            representertIdent = representert.ident,
        )
    }
}
