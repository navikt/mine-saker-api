package no.nav.personbruker.minesaker.api.digisos

import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser
import no.nav.tms.token.support.tokendings.exchange.TokendingsService

class DigiSosTokendings(
    private val tokendingsService: TokendingsService,
    private val digiSosClientId: String
){
    suspend fun exchangeToken(user: IdportenUser): String {
        return tokendingsService.exchangeToken(user.tokenString, digiSosClientId)

    }
}
