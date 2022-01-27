package no.nav.personbruker.minesaker.api.common

import com.auth0.jwt.JWT
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser
import java.security.Key
import java.time.ZonedDateTime
import java.util.*

object IdportenUserObjectMother {

    private val key: Key = Keys.secretKeyFor(SignatureAlgorithm.HS256)

    fun createIdportenUser(): IdportenUser {
        val ident = "12345"
        return createIdportenUser(ident)
    }

    fun createIdportenUser(ident: String): IdportenUser {
        val innloggingsnivaa = 4
        return createIdportenUser(ident, innloggingsnivaa)
    }

    fun createIdportenUser(ident: String, innloggingsnivaa: Int): IdportenUser {
        val inTwoMinutes = ZonedDateTime.now().plusMinutes(2)
        return createIdportenUserWithValidTokenUntil(ident, innloggingsnivaa, inTwoMinutes)
    }

    fun createIdportenUserWithValidTokenUntil(
            ident: String,
            innloggingsnivaa: Int,
            tokensUtlopstidspunkt: ZonedDateTime
    ): IdportenUser {
        val jws = Jwts.builder()
                .setSubject(ident)
                .addClaims(mutableMapOf(Pair("acr", "Level$innloggingsnivaa")) as Map<String, Any>?)
                .setExpiration(Date.from(tokensUtlopstidspunkt.toInstant()))
                .signWith(key).compact()


        val token = JWT.decode(jws)

        val expirationTime = token.expiresAt.toInstant()

        return IdportenUser(ident, innloggingsnivaa, expirationTime, token)
    }
}
