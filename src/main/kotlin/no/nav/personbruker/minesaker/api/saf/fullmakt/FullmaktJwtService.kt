package no.nav.personbruker.minesaker.api.saf.fullmakt

import com.auth0.jwk.Jwk
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.JWTVerifier
import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import java.security.interfaces.RSAPublicKey
import java.time.Instant
import java.util.*

class FullmaktJwtService(
    private val issuer: String,
    jwkString: String
) {
    companion object {
        const val FullmektigIdentClaim = "flm_id"
        const val RepresentertIdentClaim = "rep_id"
        const val RepresentertNavnClaim = "rep_nm"

        val DecodedJWT.representertIdent get() = getClaim(RepresentertIdentClaim).asString()
        val DecodedJWT.representertNavn get() = getClaim(RepresentertNavnClaim).asString()
    }

    private val rsaKey = RSAKey.parse(jwkString)
    private val publicJwk = rsaKey.publicJwk()

    fun generateJwtString(validForhold: ValidForhold): String {
        val now = Date.from(Instant.now())
        return JWTClaimsSet.Builder()
            .issuer(issuer)
            .audience(issuer)
            .issueTime(now)
            .expirationTime(Date.from(Instant.now().plusSeconds(1800)))
            .jwtID(UUID.randomUUID().toString())
            .claim(FullmektigIdentClaim, validForhold.fullmektigIdent)
            .claim(RepresentertIdentClaim, validForhold.representertIdent)
            .claim(RepresentertNavnClaim, validForhold.representertNavn)
            .build()
            .sign()
            .serialize()
    }

    fun verify(fullmektigToken: String, fullmektig: String): DecodedJWT {
        return publicJwk.fullmektigVerifier(fullmektig)
            .verify(fullmektigToken)
    }

    private fun Jwk.fullmektigVerifier(fullmektig: String): JWTVerifier =
        JWT.require(this.RSA256())
            .withAudience(issuer)
            .withIssuer(issuer)
            .withClaim(FullmektigIdentClaim, fullmektig)
            .build()

    private fun Jwk.RSA256() = Algorithm.RSA256(publicKey as RSAPublicKey, null)

    private fun RSAKey.publicJwk() = toPublicJWK().toJSONObject().let { Jwk.fromValues(it.toMap()) }

    private fun JWTClaimsSet.sign(): SignedJWT =
        SignedJWT(
            JWSHeader.Builder(JWSAlgorithm.RS256)
                .type(JOSEObjectType.JWT).build(),
            this
        ).apply {
            sign(RSASSASigner(rsaKey.toPrivateKey()))
        }
}
