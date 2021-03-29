package no.nav.personbruker.minesaker.api.debug

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.personbruker.minesaker.api.config.idportenUser
import no.nav.personbruker.minesaker.api.tokenx.TokendingsServiceWrapper

fun Route.exchangeApi(tokendingsServiceWrapper: TokendingsServiceWrapper, clusterName: String) {
    if (isRunningInDevGcp(clusterName)) {
        get("/exchange") {
            val idToken = idportenUser.tokenString

            val token = tokendingsServiceWrapper.exchangeTokenForSafSelvbetjening(idToken)

            call.respondText(token.value)
        }
    }
}

fun isRunningInDevGcp(clusterName: String): Boolean {
    return "dev-gcp" == clusterName
}
