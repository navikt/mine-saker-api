package no.nav.personbruker.minesaker.api.common.sak

import no.nav.personbruker.minesaker.api.saf.SafConsumer
import no.nav.personbruker.minesaker.api.saf.dto.out.Sakstema
import no.nav.personbruker.minesaker.api.saf.queries.HentKonkretSakstema

class SakService(
    private val safConsumer: SafConsumer
) {

    suspend fun hentSakstema(sakstemakode: String): List<Sakstema> {
        val sakstemaRequest = HentKonkretSakstema.createRequest(sakstemakode)
        return safConsumer.hentKonkretSakstema(sakstemaRequest)
    }

}
