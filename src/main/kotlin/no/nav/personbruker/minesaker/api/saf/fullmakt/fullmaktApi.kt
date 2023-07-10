package no.nav.personbruker.minesaker.api.saf.fullmakt

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.receive
import no.nav.personbruker.minesaker.api.config.idportenUser

fun Route.fullmaktApi(fullmaktService: FullmaktService, jwtService: FullmektigJwtService) {
    get("/fullmakt/forhold") {
        call.respond(fullmaktService.getFullmaktForhold(idportenUser))
    }

    post("/fullmakt/representert") {
        val representert = call.represertIdent()

        fullmaktService.validateFullmaktsForhold(idportenUser, representert)

        val fullmektigToken = jwtService.generateJwtString(fullmektig = idportenUser.ident, representert = representert)

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
