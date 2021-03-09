package no.nav.personbruker.minesaker.api.tokenx

import no.nav.tms.token.support.tokendings.exchange.TokendingsService

class TokendingsServiceWrapper(
        private val tokendingsService: TokendingsService,
        private val safselvbetjeningClientId: String
){
    suspend fun exchangeTokenForSafSelvbetjening(token: String): String {
        return tokendingsService.exchangeToken(token, safselvbetjeningClientId)
    }
}
