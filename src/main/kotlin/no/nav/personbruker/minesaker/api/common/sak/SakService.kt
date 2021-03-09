package no.nav.personbruker.minesaker.api.common.sak

import no.nav.personbruker.minesaker.api.saf.SafConsumer
import no.nav.personbruker.minesaker.api.saf.domain.Sakstema
import no.nav.personbruker.minesaker.api.saf.domain.Sakstemakode
import no.nav.personbruker.minesaker.api.saf.journalposter.JournalposterRequest
import no.nav.personbruker.minesaker.api.saf.sakstemaer.SakstemaerRequest
import no.nav.personbruker.minesaker.api.tokenx.TokendingsServiceWrapper
import no.nav.tms.token.support.idporten.user.IdportenUser

class SakService(
    private val safConsumer: SafConsumer,
    private val tokendingsWrapper: TokendingsServiceWrapper
) {

    suspend fun hentSakstemaer(user: IdportenUser): List<Sakstema> {
        val exchangedToken = exchangeToken(user)
        val sakstemaerRequest = SakstemaerRequest.create(user.ident)
        return safConsumer.hentSakstemaer(sakstemaerRequest, exchangedToken)
    }

    suspend fun hentJournalposterForSakstema(user: IdportenUser, sakstema: Sakstemakode): List<Sakstema> {
        val exchangedToken = exchangeToken(user)
        val journalposterRequest = JournalposterRequest.create(user.ident, sakstema)
        return safConsumer.hentJournalposter(user.ident, journalposterRequest, exchangedToken)
    }

    private suspend fun exchangeToken(user: IdportenUser): String {
        return tokendingsWrapper.exchangeTokenForSafSelvbetjening(user.tokenString)
    }

}
