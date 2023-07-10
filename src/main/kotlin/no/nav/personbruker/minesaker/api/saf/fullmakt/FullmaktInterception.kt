package no.nav.personbruker.minesaker.api.saf.fullmakt

import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.ktor.server.auth.*
import io.ktor.util.*
import mu.KotlinLogging
import no.nav.tms.token.support.idporten.sidecar.authentication.IdPortenTokenPrincipal

class FullmaktInterception(val fullmektigJwtService: FullmektigJwtService) {
    companion object {
        private const val FullmaktInterceptor = "fullmakt-interceptor"
        val FullmaktAttribute = AttributeKey<Fullmakt>("fullmakt_attribute")
    }

    private val log = KotlinLogging.logger {}

    val interceptor = createApplicationPlugin(name = FullmaktInterceptor) {
        requireNotNull(application.pluginOrNull(Authentication)) { "Fullmaktinterceptor must be installed after Authentication" }

        on(CallSetup) { call ->
            val fullmektigToken = call.request.cookies[FullmaktCookie]
            if (fullmektigToken != null) {
                try {
                    val ident = call.userIdent

                    val jwt = fullmektigJwtService.verify(fullmektigToken, ident)
                    val representert = jwt.getClaim(FullmektigJwtService.RepresentertClaim).asString()

                    val fullmakt = Fullmakt(
                        fullmektig = ident,
                        representert = representert
                    )

                    call.attributes.put(FullmaktAttribute, fullmakt)
                } catch (e: Exception) {
                    call.response.cookies.expireFullmakt()
                }
            }
        }
    }
}

private val ApplicationCall.userIdent get() = principal<IdPortenTokenPrincipal>()?.accessToken?.getClaim("pid")?.asString()!!

data class Fullmakt(
    val fullmektig: String,
    val representert: String
): Principal
