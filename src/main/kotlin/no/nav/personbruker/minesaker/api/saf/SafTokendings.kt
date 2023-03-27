package no.nav.personbruker.minesaker.api.saf

import no.nav.personbruker.minesaker.api.domain.AuthenticatedUser
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser
import no.nav.tms.token.support.tokendings.exchange.TokendingsService

class SafTokendings(
    private val tokendingsService: TokendingsService,
    private val safselvbetjeningClientId: String
) {
    suspend fun exchangeToken(user: IdportenUser): String {
        return tokendingsService.exchangeToken(user.tokenString, safselvbetjeningClientId)
    }

    suspend fun exchangeToken(user: AuthenticatedUser): String {
        return tokendingsService.exchangeToken(user.tokenString, safselvbetjeningClientId)
    }
}

class TokendingsError: Throwable()
