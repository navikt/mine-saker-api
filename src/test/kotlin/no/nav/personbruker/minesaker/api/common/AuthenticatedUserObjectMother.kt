package no.nav.personbruker.minesaker.api.common

import no.nav.personbruker.minesaker.api.domain.AuthenticatedUser

object AuthenticatedUserObjectMother {

    fun createTokenXUser(): AuthenticatedUser {
        val ident = "12345"
        val user = TokenXUserObjectMother.createTokenXUser(ident)
        return AuthenticatedUser.createTokenXUser(user)
    }

}
