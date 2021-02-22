package no.nav.personbruker.minesaker.api.common.sak

import no.nav.personbruker.minesaker.api.common.AuthenticatedUser
import no.nav.personbruker.minesaker.api.saf.SafConsumer
import no.nav.personbruker.minesaker.api.saf.dto.out.Sakstema
import no.nav.personbruker.minesaker.api.saf.requests.JournalposterRequest
import no.nav.personbruker.minesaker.api.saf.requests.SakstemaRequest

class SakService(
    private val safConsumer: SafConsumer
) {

    suspend fun hentSakstemaer(user: AuthenticatedUser): List<Sakstema> {
        val sakstemaRequest = SakstemaRequest.create(user.ident)
        return safConsumer.hentSakstemaer(sakstemaRequest)
    }

    suspend fun hentJournalposterForSakstema(user: AuthenticatedUser, sakstemakode: String): List<Sakstema> {
        val journalposterRequest = JournalposterRequest.create(user.ident, sakstemakode)
        return safConsumer.hentJournalposter(journalposterRequest)
    }

}
