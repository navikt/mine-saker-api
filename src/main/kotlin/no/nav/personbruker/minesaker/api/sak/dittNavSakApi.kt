package no.nav.personbruker.minesaker.api.sak

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import no.nav.personbruker.minesaker.api.config.tokenXUser
import no.nav.personbruker.minesaker.api.domain.AuthenticatedUser

fun Route.dittNavSakApi(
    service: SakService
) {

    val log = KotlinLogging.logger { }

    get("/sakstemaer/sistendret") {
        val user = AuthenticatedUser.createTokenXUser(tokenXUser)
        val result = service.hentSakstemaer(user)
        if (result.hasErrors()) {
            log.warn("En eller flere kilder feilet: ${result.errors()}. Klienten får en passende http-svarkode.")
        }
        call.respond(result.determineHttpCode(), result.recentlyModifiedSakstemaResults())
    }

}
