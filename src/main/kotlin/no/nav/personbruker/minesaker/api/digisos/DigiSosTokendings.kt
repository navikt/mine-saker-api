package no.nav.personbruker.minesaker.api.digisos

import no.nav.personbruker.minesaker.api.domain.AuthenticatedUser
import no.nav.tms.token.support.tokendings.exchange.TokendingsService

class DigiSosTokendings(
    private val tokendingsService: TokendingsService,
    private val digiSosClientId: String
){
    suspend fun exchangeToken(user: AuthenticatedUser): String {
        return tokendingsService.exchangeToken(user.tokenString, digiSosClientId)
    }
}
