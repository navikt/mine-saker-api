package no.nav.personbruker.minesaker.api.digisos

import no.nav.personbruker.minesaker.api.domain.AuthenticatedUser
import no.nav.personbruker.minesaker.api.tokenx.AccessToken
import no.nav.tms.token.support.tokendings.exchange.TokendingsService

class DigiSosTokendings(
    private val tokendingsService: TokendingsService,
    private val digiSosClientId: String
){
    suspend fun exchangeToken(user: AuthenticatedUser): AccessToken {
        return AccessToken(tokendingsService.exchangeToken(user.tokenString, digiSosClientId))
    }
}
