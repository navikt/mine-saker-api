package no.nav.personbruker.minesaker.api.saf.fullmakt

import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.SignedJWT
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.util.*
import no.nav.personbruker.minesaker.api.saf.fullmakt.FullmaktJwtService.Companion.representertIdent
import no.nav.personbruker.minesaker.api.saf.fullmakt.FullmaktJwtService.Companion.representertNavn

class FullmaktInterception(private val fullmaktJwtService: FullmaktJwtService) {
    companion object {
        val FullmaktAttribute = AttributeKey<Fullmakt>("fullmakt_attribute")
    }

    private val log = KotlinLogging.logger {}

    val interceptor = createApplicationPlugin(name = "fullmakt-interceptor") {
        requireNotNull(application.pluginOrNull(Authentication)) { "Fullmaktinterceptor must be installed after Authentication" }

        onCall { call ->
            val fullmektigToken = call.request.cookies[FullmaktCookie]
            val accessToken = call.request.authHeader()
            if (fullmektigToken != null && accessToken != null) {
                try {
                    val ident = accessToken.userIdent

                    val jwt = fullmaktJwtService.verify(fullmektigToken, ident)

                    val fullmakt = Fullmakt(
                        fullmektigIdent = ident,
                        representertIdent = jwt.representertIdent,
                        representertNavn = jwt.representertNavn,
                    )

                    call.attributes.put(FullmaktAttribute, fullmakt)
                } catch (e: Exception) {
                    log.warn(e) { "Fullmektig-feil" }
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
    val fullmektigIdent: String,
    val representertIdent: String,
    val representertNavn: String
): Principal
