package no.nav.personbruker.minesaker.api.sak

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.personbruker.minesaker.api.common.ExceptionResponseHandler
import no.nav.personbruker.minesaker.api.config.tokenXUser
import no.nav.personbruker.minesaker.api.domain.AuthenticatedUser

fun Route.dittNavSakApi(
    service: SakService
) {

    get("/sakstemaer/sistendret") {
        try {
            val user = AuthenticatedUser.createTokenXUser(tokenXUser)
            val result = service.hentSakstemaer(user)
            if(result.hasErrors()) {
                log.warn("En eller flere kilder feilet: ${result.errors()}. Klienten får en passende http-svarkode.")
            }
            call.respond(result.determineHttpCode(), result.theTwoMostRecentlyModifiedResults())

        } catch (exception: Exception) {
            val errorCode = ExceptionResponseHandler.logExceptionAndDecideErrorResponseCode(log, exception)
            call.respond(errorCode)
        }
    }

}
