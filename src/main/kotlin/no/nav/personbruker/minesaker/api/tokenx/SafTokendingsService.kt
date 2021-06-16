package no.nav.personbruker.minesaker.api.tokenx

import no.nav.tms.token.support.tokendings.exchange.TokendingsService

class SafTokendingsService(
    private val tokendingsService: TokendingsService,
    private val safselvbetjeningClientId: String
){
    suspend fun exchangeTokenForSafSelvbetjening(token: String): AccessToken {
        return AccessToken(tokendingsService.exchangeToken(token, safselvbetjeningClientId))
    }
}
