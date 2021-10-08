package no.nav.personbruker.minesaker.api.common

import com.auth0.jwt.JWT
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import no.nav.tms.token.support.tokenx.validation.user.TokenXUser
import java.security.Key
import java.time.ZonedDateTime
import java.util.*

object TokenXUserObjectMother {

    private val key: Key = Keys.secretKeyFor(SignatureAlgorithm.HS256)

    fun createTokenXUser(): TokenXUser {
        val ident = "12345"
        return createTokenXUser(ident)
    }

    fun createTokenXUser(ident: String): TokenXUser {
        val innloggingsnivaa = 4
        return createTokenXUser(ident, innloggingsnivaa)
    }

    fun createTokenXUser(ident: String, innloggingsnivaa: Int): TokenXUser {
        val inTwoMinutes = ZonedDateTime.now().plusMinutes(2)
        return createIdportenUserWithValidTokenUntil(ident, innloggingsnivaa, inTwoMinutes)
    }

    fun createIdportenUserWithValidTokenUntil(
            ident: String,
            innloggingsnivaa: Int,
            tokensUtlopstidspunkt: ZonedDateTime
    ): TokenXUser {
        val jws = Jwts.builder()
                .setSubject(ident)
                .addClaims(mutableMapOf(Pair("acr", "Level$innloggingsnivaa")) as Map<String, Any>?)
                .setExpiration(Date.from(tokensUtlopstidspunkt.toInstant()))
                .signWith(key).compact()


        val token = JWT.decode(jws)

        val expirationTime = token.expiresAt.toInstant()

        return TokenXUser(ident, innloggingsnivaa, expirationTime, token)
    }

}
