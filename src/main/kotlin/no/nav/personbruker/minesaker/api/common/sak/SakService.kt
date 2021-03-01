package no.nav.personbruker.minesaker.api.common.sak

import no.nav.personbruker.minesaker.api.common.AuthenticatedUser
import no.nav.personbruker.minesaker.api.saf.SafConsumer
import no.nav.personbruker.minesaker.api.saf.domain.Sakstema
import no.nav.personbruker.minesaker.api.saf.journalposter.JournalposterRequest
import no.nav.personbruker.minesaker.api.saf.sakstemaer.SakstemaerRequest

class SakService(
    private val safConsumer: SafConsumer
) {

    suspend fun hentSakstemaer(user: AuthenticatedUser): List<Sakstema> {
        val sakstemaerRequest = SakstemaerRequest.create(user.ident)
        return safConsumer.hentSakstemaer(sakstemaerRequest)
    }

    suspend fun hentJournalposterForSakstema(user: AuthenticatedUser, sakstemakode: String): List<Sakstema> {
        val journalposterRequest = JournalposterRequest.create(user.ident, sakstemakode)
        return safConsumer.hentJournalposter(journalposterRequest)
    }

}
