package no.nav.tms.minesaker.api.setup

import no.nav.tms.token.support.entraid.token.fetcher.EntraIdTokenFetcher


class SystemTokenFetcher(
    private val tokenFetcher: EntraIdTokenFetcher,
    private val pdlApiClientId: String
) {
    suspend fun pdlApiToken(): String {
        return tokenFetcher.getAccessToken(pdlApiClientId)
    }
}
