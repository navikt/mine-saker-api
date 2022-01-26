package no.nav.personbruker.minesaker.api.saf

import no.nav.personbruker.minesaker.api.domain.AuthenticatedUser
import no.nav.personbruker.minesaker.api.tokenx.AccessToken
import no.nav.tms.token.support.idporten.user.IdportenUser
import no.nav.tms.token.support.tokendings.exchange.TokendingsService

class SafTokendings(
    private val tokendingsService: TokendingsService,
    private val safselvbetjeningClientId: String
) {
    suspend fun exchangeToken(user: IdportenUser): AccessToken {
        return AccessToken(tokendingsService.exchangeToken(user.tokenString, safselvbetjeningClientId))
    }

    suspend fun exchangeToken(user: AuthenticatedUser): AccessToken {
        return AccessToken(tokendingsService.exchangeToken(user.tokenString, safselvbetjeningClientId))
    }
}
