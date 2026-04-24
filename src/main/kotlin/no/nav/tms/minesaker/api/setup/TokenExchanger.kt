package no.nav.tms.minesaker.api.setup

import no.nav.tms.token.support.user.token.exchange.UserTokenExchangeService

class TokenExchanger(
    private val exchangeService: UserTokenExchangeService,
    private val safselvbetjeningClientId: String,
    private val digiSosClientId: String,
    private val pdlFullmaktClientId: String,
    private val pdlApiClientId: String
) {
    suspend fun safToken(accessToken: String): String {
        return exchangeService.exchangeToken(accessToken, safselvbetjeningClientId)
    }

    suspend fun digisosToken(accessToken: String): String {
        return exchangeService.exchangeToken(accessToken, digiSosClientId)
    }

    suspend fun pdlFullmaktToken(accessToken: String): String {
        return exchangeService.exchangeToken(accessToken, pdlFullmaktClientId)
    }

    suspend fun pdlApiToken(accessToken: String): String {
        return exchangeService.exchangeToken(accessToken, pdlApiClientId)
    }
}
