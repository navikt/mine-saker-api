package no.nav.personbruker.minesaker.api.saf.fullmakt

import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.SignedJWT
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.util.*

class FullmaktInterception(private val redisService: FullmaktRedisService) {
    companion object {
        val FullmaktAttribute = AttributeKey<FullmaktWrapper>("fullmakt_attribute")
    }

    private val log = KotlinLogging.logger {}

    val interceptor = createApplicationPlugin(name = "fullmakt-interceptor") {
        requireNotNull(application.pluginOrNull(Authentication)) { "Fullmaktinterceptor must be installed after Authentication" }

        onCall { call ->

            val accessToken = call.request.authHeader()

            if (accessToken != null) {

                val subject = accessToken.subjectClaim

                try {
                    val wrapper = FullmaktWrapper {
                        redisService.getForhold(subject)
                    }

                    call.attributes.put(FullmaktAttribute, wrapper)
                } catch (e: Exception) {
                    log.warn(e) { "Fullmektig-feil" }
                    redisService.clearForhold(subject)
                }
            }
        }
    }
}

private val JWT.subjectClaim get() = jwtClaimsSet.subject

private val authRegex = "Bearer (.+)".toRegex()

private fun ApplicationRequest.authHeader(): JWT? {
    return headers[HttpHeaders.Authorization]
        ?.let{ authRegex.matchEntire(it)?.destructured }
        ?.let { (token) -> token }
        ?.let { SignedJWT.parse(it) }
}

class FullmaktWrapper (
    private val fullmaktProvider: () -> ValidForhold?
) {
    val fullmakt: Fullmakt? by lazy {
        fullmaktProvider.invoke()
            ?.let {
                Fullmakt(
                    fullmektigIdent = it.fullmektigIdent,
                    representertIdent = it.representertIdent,
                    representertNavn = it.representertNavn
                )
            }
    }

}

data class Fullmakt(
    val fullmektigIdent: String,
    val representertIdent: String,
    val representertNavn: String
): Principal
