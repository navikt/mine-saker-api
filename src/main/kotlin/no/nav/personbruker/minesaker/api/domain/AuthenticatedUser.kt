package no.nav.personbruker.minesaker.api.domain

import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser
import no.nav.tms.token.support.tokenx.validation.user.TokenXUser

data class AuthenticatedUser(
    val ident: String,
    val tokenString: String
) {

    companion object {
        fun createIdPortenUser(user: IdportenUser): AuthenticatedUser {
            return AuthenticatedUser(user.ident, user.tokenString)
        }

        fun createTokenXUser(user: TokenXUser): AuthenticatedUser {
            return AuthenticatedUser(user.ident, user.tokenString)
        }
    }

}
