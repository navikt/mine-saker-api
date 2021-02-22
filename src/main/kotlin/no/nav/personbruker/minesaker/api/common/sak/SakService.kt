package no.nav.personbruker.minesaker.api.common.sak

import no.nav.personbruker.minesaker.api.common.AuthenticatedUser
import no.nav.personbruker.minesaker.api.saf.SafConsumer
import no.nav.personbruker.minesaker.api.saf.dto.out.Sakstema
import no.nav.personbruker.minesaker.api.saf.queries.HentJournalposter
import no.nav.personbruker.minesaker.api.saf.queries.HentSakstema

class SakService(
    private val safConsumer: SafConsumer
) {

    suspend fun hentSakstemaer(user: AuthenticatedUser): List<Sakstema> {
        val sakstemaRequest = HentSakstema.createRequest(user.ident)
        return safConsumer.hentSakstemaer(sakstemaRequest)
    }

    suspend fun hentJournalposterForSakstema(user: AuthenticatedUser, sakstemakode: String): List<Sakstema> {
        val journalposterRequest = HentJournalposter.createRequest(user.ident, sakstemakode)
        return safConsumer.hentJournalposter(journalposterRequest)
    }

}
