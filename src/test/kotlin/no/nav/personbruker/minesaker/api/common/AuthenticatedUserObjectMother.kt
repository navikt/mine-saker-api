package no.nav.personbruker.minesaker.api.common

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import no.nav.personbruker.minesaker.api.saf.domain.Fodselsnummer
import no.nav.security.token.support.core.jwt.JwtToken
import java.security.Key
import java.time.ZonedDateTime
import java.util.*

object AuthenticatedUserObjectMother {

    private val key: Key = Keys.secretKeyFor(SignatureAlgorithm.HS256)

    fun createAuthenticatedUser(): AuthenticatedUser {
        val ident = Fodselsnummer("12345")
        return createAuthenticatedUser(ident)
    }

    fun createAuthenticatedUser(ident: Fodselsnummer): AuthenticatedUser {
        val innloggingsnivaa = 4
        return createAuthenticatedUser(ident, innloggingsnivaa)
    }

    fun createAuthenticatedUser(ident: Fodselsnummer, innloggingsnivaa: Int): AuthenticatedUser {
        val inTwoMinutes = ZonedDateTime.now().plusMinutes(2)
        return createAuthenticatedUserWithValidTokenUntil(ident, innloggingsnivaa, inTwoMinutes, null)
    }

    fun createAuthenticatedUserWithValidTokenUntil(
        ident: Fodselsnummer,
        innloggingsnivaa: Int,
        tokensUtlopstidspunkt: ZonedDateTime,
        auxiliaryToken: String?
    ): AuthenticatedUser {
        val jws = Jwts.builder()
                .setSubject(ident.value)
                .addClaims(mutableMapOf(Pair("acr", "Level$innloggingsnivaa")) as Map<String, Any>?)
                .setExpiration(Date.from(tokensUtlopstidspunkt.toInstant()))
                .signWith(key).compact()
        val token = JwtToken(jws)
        val expirationTime = token.jwtTokenClaims
                                                .expirationTime
                                                .toInstant()
        return AuthenticatedUser(ident, innloggingsnivaa, token.tokenAsString, expirationTime, auxiliaryToken)
    }

    fun createAuthenticatedUserWithAuxiliaryToken(loginLevel: Int, auxiliaryToken: String?): AuthenticatedUser {
        val ident = Fodselsnummer("123")
        val inTwoMinutes = ZonedDateTime.now().plusMinutes(2)

        return createAuthenticatedUserWithValidTokenUntil(ident, loginLevel, inTwoMinutes, auxiliaryToken)
    }
}
