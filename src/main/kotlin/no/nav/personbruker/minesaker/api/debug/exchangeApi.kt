package no.nav.personbruker.minesaker.api.debug

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.personbruker.minesaker.api.config.Environment
import no.nav.personbruker.minesaker.api.config.idportenUser
import no.nav.personbruker.minesaker.api.config.isRunningInDev
import no.nav.personbruker.minesaker.api.digisos.DigiSosTokendings
import no.nav.personbruker.minesaker.api.domain.AuthenticatedUser
import no.nav.personbruker.minesaker.api.saf.SafTokendings

fun Route.exchangeApi(safTokendings: SafTokendings, digiSosTokendings: DigiSosTokendings, environment : Environment) {

    if (environment.isRunningInDev()) {

        get("/exchange") {
            val token = safTokendings.exchangeToken(idportenUser)

            call.respondText(token.value)
        }

        get("/exchange/saf") {
            val token = safTokendings.exchangeToken(idportenUser)

            call.respondText(token.value)
        }

        get("/exchange/digisos") {
            val user = AuthenticatedUser.createIdPortenUser(idportenUser)
            val token = digiSosTokendings.exchangeToken(user)

            call.respondText(token.value)
        }

    }

}
