package no.nav.personbruker.minesaker.api.saf.fullmakt

import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.SignedJWT
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
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

        onCall { call ->
            val fullmektigToken = call.request.cookies[FullmaktCookie]
            val accessToken = call.request.authHeader()
            if (fullmektigToken != null && accessToken != null) {
                try {
                    val ident = accessToken.userIdent

                    log.info("Token: $fullmektigToken")

                    val jwt = fullmektigJwtService.verify(fullmektigToken, ident)
                    val representert = jwt.getClaim(FullmektigJwtService.RepresentertClaim).asString()

                    val fullmakt = Fullmakt(
                        fullmektig = ident,
                        representert = representert
                    )

                    call.attributes.put(FullmaktAttribute, fullmakt)
                } catch (e: Exception) {
                    log.warn("Fullmektig-feil", e)
                    call.response.cookies.expireFullmakt()
                }
            }
        }
    }
}

private val JWT.userIdent get() = jwtClaimsSet.getClaim("pid")!! as String

private val authRegex = "Bearer (.+)".toRegex()

private fun ApplicationRequest.authHeader(): JWT? {
    return headers[HttpHeaders.Authorization]
        ?.let{ authRegex.matchEntire(it)?.destructured }
        ?.let { (token) -> token }
        ?.let { SignedJWT.parse(it) }
}

data class Fullmakt(
    val fullmektig: String,
    val representert: String
): Principal
