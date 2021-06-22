package no.nav.personbruker.minesaker.api.debug

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.personbruker.minesaker.api.config.idportenUser
import no.nav.personbruker.minesaker.api.digisos.DigiSosTokendings
import no.nav.personbruker.minesaker.api.saf.SafTokendings

fun Route.exchangeApi(safTokendings: SafTokendings, digiSosTokendings: DigiSosTokendings, clusterName: String) {

    if (isRunningInDevGcp(clusterName)) {

        get("/exchange") {
            val token = safTokendings.exchangeToken(idportenUser)

            call.respondText(token.value)
        }

        get("/exchange/saf") {
            val token = safTokendings.exchangeToken(idportenUser)

            call.respondText(token.value)
        }

        get("/exchange/digisos") {
            val token = digiSosTokendings.exchangeToken(idportenUser)

            call.respondText(token.value)
        }

    }

}

fun isRunningInDevGcp(clusterName: String): Boolean {
    return "dev-gcp" == clusterName
}
