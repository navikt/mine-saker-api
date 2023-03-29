package no.nav.personbruker.minesaker.api.domain

import io.kotest.matchers.shouldBe
import no.nav.personbruker.minesaker.api.common.IdportenTestUser
import no.nav.personbruker.minesaker.api.common.TokenXTestUser
import org.junit.jupiter.api.Test

internal class AuthenticatedUserTest {

    @Test
    fun `Skal kunne lage variant for ID-Porten`() {
        val expectedUser = IdportenTestUser.createIdportenUser()
        val innloggetBruker = AuthenticatedUser.createIdPortenUser(expectedUser)

        innloggetBruker.ident shouldBe expectedUser.ident
        innloggetBruker.tokenString shouldBe expectedUser.tokenString
    }

    @Test
    fun `Skal kunne lage variant for TokenX`() {
        val expectedUser = TokenXTestUser.createTokenXUser()
        val innloggetBruker = AuthenticatedUser.createTokenXUser(expectedUser)

        innloggetBruker.ident shouldBe expectedUser.ident
        innloggetBruker.tokenString shouldBe expectedUser.tokenString
    }

}
