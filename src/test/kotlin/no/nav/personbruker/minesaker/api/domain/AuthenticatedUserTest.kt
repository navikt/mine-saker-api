package no.nav.personbruker.minesaker.api.domain

import no.nav.personbruker.minesaker.api.common.IdportenUserObjectMother
import no.nav.personbruker.minesaker.api.common.TokenXUserObjectMother
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

internal class AuthenticatedUserTest {

    @Test
    fun `Skal kunne lage variant for ID-Porten`() {
        val expectedUser = IdportenUserObjectMother.createIdportenUser()
        val innloggetBruker = AuthenticatedUser.createIdPortenUser(expectedUser)

        innloggetBruker.ident `should be equal to` expectedUser.ident
        innloggetBruker.tokenString `should be equal to` expectedUser.tokenString
    }

    @Test
    fun `Skal kunne lage variant for TokenX`() {
        val expectedUser = TokenXUserObjectMother.createTokenXUser()
        val innloggetBruker = AuthenticatedUser.createTokenXUser(expectedUser)

        innloggetBruker.ident `should be equal to` expectedUser.ident
        innloggetBruker.tokenString `should be equal to` expectedUser.tokenString
    }

}
