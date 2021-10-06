package no.nav.personbruker.minesaker.api.sak

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.personbruker.minesaker.api.common.ExceptionResponseHandler
import no.nav.personbruker.minesaker.api.config.idportenUser

fun Route.dittNavSakApi(
    service: SakService
) {

    get("/sakstemaer/sistendret") {
        try {
            val result = service.hentSakstemaer(idportenUser)
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
