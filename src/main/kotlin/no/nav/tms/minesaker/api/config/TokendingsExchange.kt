package no.nav.tms.minesaker.api.config

import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser
import no.nav.tms.token.support.tokendings.exchange.TokendingsService

class TokendingsExchange(
    private val tokendingsService: TokendingsService,
    private val safselvbetjeningClientId: String,
    private val digiSosClientId: String,
    private val pdlFullmaktClientId: String,
    private val pdlApiClientId: String
) {
    suspend fun safToken(user: IdportenUser): String {
        return tokendingsService.exchangeToken(user.tokenString, safselvbetjeningClientId)
    }

    suspend fun digisosToken(user: IdportenUser): String {
        return tokendingsService.exchangeToken(user.tokenString, digiSosClientId)
    }

    suspend fun pdlFullmaktToken(user: IdportenUser): String {
        return tokendingsService.exchangeToken(user.tokenString, pdlFullmaktClientId)
    }

    suspend fun pdlApiToken(user: IdportenUser): String {
        return tokendingsService.exchangeToken(user.tokenString, pdlApiClientId)
    }
}
