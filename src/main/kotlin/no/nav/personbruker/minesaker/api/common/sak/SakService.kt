package no.nav.personbruker.minesaker.api.common.sak

import no.nav.personbruker.minesaker.api.common.AuthenticatedUser
import no.nav.personbruker.minesaker.api.saf.SafConsumer
import no.nav.personbruker.minesaker.api.saf.dto.out.Sakstema
import no.nav.personbruker.minesaker.api.saf.queries.HentKonkretSakstema
import no.nav.personbruker.minesaker.api.saf.queries.HentSaker

class SakService(
    private val safConsumer: SafConsumer
) {

    suspend fun hentSaker(user: AuthenticatedUser): List<Sakstema> {
        val sakerRequest = HentSaker.createRequest(user.ident)
        return safConsumer.hentSaker(sakerRequest)
    }

    suspend fun hentSakstema(user: AuthenticatedUser, sakstemakode: String): List<Sakstema> {
        val sakstemaRequest = HentKonkretSakstema.createRequest(user.ident, sakstemakode)
        return safConsumer.hentKonkretSakstema(sakstemaRequest)
    }

}
