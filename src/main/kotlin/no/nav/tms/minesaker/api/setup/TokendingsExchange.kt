package no.nav.tms.minesaker.api.setup

import no.nav.tms.token.support.user.token.exchange.UserTokenExchanger


class TokendingsExchange(
    private val tokenExchanger: UserTokenExchanger,
    private val safselvbetjeningClientId: String,
    private val digiSosClientId: String,
    private val pdlFullmaktClientId: String,
    private val pdlApiClientId: String
) {
    suspend fun safToken(accessToken: String): String {
        return tokenExchanger.exchangeToken(accessToken, safselvbetjeningClientId)
    }

    suspend fun digisosToken(accessToken: String): String {
        return tokenExchanger.exchangeToken(accessToken, digiSosClientId)
    }

    suspend fun pdlFullmaktToken(accessToken: String): String {
        return tokenExchanger.exchangeToken(accessToken, pdlFullmaktClientId)
    }

    suspend fun pdlApiToken(accessToken: String): String {
        return tokenExchanger.exchangeToken(accessToken, pdlApiClientId)
    }
}
