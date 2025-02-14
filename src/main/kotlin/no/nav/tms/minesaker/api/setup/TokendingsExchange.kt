package no.nav.tms.minesaker.api.setup

import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser
import no.nav.tms.token.support.tokendings.exchange.TokendingsService

class TokendingsExchange(
    private val tokendingsService: TokendingsService,
    private val safselvbetjeningClientId: String,
    private val digiSosClientId: String,
    private val pdlFullmaktClientId: String,
    private val pdlApiClientId: String
) {
    suspend fun safToken(accessToken: String): String {
        return tokendingsService.exchangeToken(accessToken, safselvbetjeningClientId)
    }

    suspend fun digisosToken(accessToken: String): String {
        return tokendingsService.exchangeToken(accessToken, digiSosClientId)
    }

    suspend fun pdlFullmaktToken(accessToken: String): String {
        return tokendingsService.exchangeToken(accessToken, pdlFullmaktClientId)
    }

    suspend fun pdlApiToken(accessToken: String): String {
        return tokendingsService.exchangeToken(accessToken, pdlApiClientId)
    }
}
