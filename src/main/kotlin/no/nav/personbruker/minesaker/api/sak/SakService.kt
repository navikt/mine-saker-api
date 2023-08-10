package no.nav.personbruker.minesaker.api.sak

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.personbruker.minesaker.api.digisos.DigiSosConsumer
import no.nav.personbruker.minesaker.api.domain.Sakstema
import no.nav.personbruker.minesaker.api.domain.Sakstemakode
import no.nav.personbruker.minesaker.api.saf.SafConsumer
import no.nav.personbruker.minesaker.api.config.TokendingsExchange
import no.nav.personbruker.minesaker.api.saf.journalposter.JournalposterRequest
import no.nav.personbruker.minesaker.api.saf.sakstemaer.SakstemaerRequest
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser

class SakService(
    private val safConsumer: SafConsumer,
    private val tokendingsExchange: TokendingsExchange,
    private val digiSosConsumer: DigiSosConsumer,
) {

    private val log = KotlinLogging.logger { }

    suspend fun hentSakstemaer(user: IdportenUser): SakstemaResult = withContext(Dispatchers.IO) {
        val sakstemaerFraSaf = async {
            hentSakstemaerFraSaf(user)
        }
        val sakstemaerFraDigiSos = async {
            hentSakstemaerFraDigiSos(user)
        }
        sakstemaerFraSaf.await() + sakstemaerFraDigiSos.await()
    }

    suspend fun hentSakstemaerFraSaf(user: IdportenUser): SakstemaResult {
        val exchangedToken = tokendingsExchange.safToken(user)
        return safConsumer.hentSakstemaer(SakstemaerRequest.create(user.ident), exchangedToken)
    }

    suspend fun hentSakstemaerFraDigiSos(user: IdportenUser): SakstemaResult {
        val exchangedToken = tokendingsExchange.digisosToken(user)
        return digiSosConsumer.hentSakstemaer(exchangedToken)
    }

    suspend fun hentJournalposterForSakstema(user: IdportenUser, sakstema: Sakstemakode): List<Sakstema> {
        val exchangedToken = tokendingsExchange.safToken(user)
        val journalposterRequest = JournalposterRequest.create(user.ident, sakstema)
        return safConsumer.hentJournalposter(user.ident, journalposterRequest, exchangedToken)
    }

    suspend fun hentDokument(
        user: IdportenUser,
        journapostId: String,
        dokumentinfoId: String
    ): ByteArray {
        log.info { "Henter dokument $dokumentinfoId fra journalposten $journapostId" }
        val exchangedToken = tokendingsExchange.safToken(user)
        return safConsumer.hentDokument(journapostId, dokumentinfoId, exchangedToken)
    }

}
