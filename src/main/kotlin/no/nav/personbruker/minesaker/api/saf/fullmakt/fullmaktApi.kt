package no.nav.personbruker.minesaker.api.saf.fullmakt

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.receive
import no.nav.personbruker.minesaker.api.config.idportenUser

fun Route.fullmaktApi(fullmaktService: FullmaktService, jwtService: FullmaktJwtService) {
    get("/fullmakt/info") {
        val fullmaktSession = call.fullmaktAttribute

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
            call.response.cookies.expireFullmakt()
            call.respond(HttpStatusCode.OK)
        } else {
            val validForhold = fullmaktService.validateFullmaktsForhold(idportenUser, representert)

            val fullmektigToken = jwtService.generateJwtString(validForhold)

            call.response.cookies.append(
                FullmaktCookie,
                fullmektigToken,
                maxAge = 3600L,
                httpOnly = true,
                path = "/mine-saker-api"
            )
            call.respond(HttpStatusCode.OK)
        }
    }

    get("/fullmakt/token") {
        call.respond(fullmaktService.token(idportenUser))
    }
}

private val ApplicationCall.fullmaktAttribute get() =
    attributes.getOrNull(FullmaktInterception.FullmaktAttribute)

fun ResponseCookies.expireFullmakt() = append(
    FullmaktCookie,
    "",
    maxAge = 0L,
    httpOnly = true,
    path = "/mine-saker-api"
)

const val FullmaktCookie = "mine-saker-api.fullmakt"

private suspend fun ApplicationCall.represertIdent() = receive<Representert>().ident

private data class Representert(
    val ident: String
)

private data class FullmaktInfo(
    val viserRepresentertesData: Boolean,
    val representertNavn: String? = null
)
