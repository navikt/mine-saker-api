package no.nav.personbruker.minesaker.api.config

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.personbruker.minesaker.api.saf.SafTokendings

fun Route.debugApi(
    safTokendings: SafTokendings
) {
    get("/exchange") {
        val exchangedToken = safTokendings.exchangeToken(idportenUser)

        call.respond(exchangedToken)
    }
}
