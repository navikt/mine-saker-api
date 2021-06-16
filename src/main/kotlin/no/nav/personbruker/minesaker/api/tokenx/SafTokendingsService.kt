package no.nav.personbruker.minesaker.api.tokenx

import no.nav.tms.token.support.idporten.user.IdportenUser
import no.nav.tms.token.support.tokendings.exchange.TokendingsService

class SafTokendingsService(
    private val tokendingsService: TokendingsService,
    private val safselvbetjeningClientId: String
){
    suspend fun exchangeToken(user: IdportenUser): AccessToken {
        return AccessToken(tokendingsService.exchangeToken(user.tokenString, safselvbetjeningClientId))
    }
}
