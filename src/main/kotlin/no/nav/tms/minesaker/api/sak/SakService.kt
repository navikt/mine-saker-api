package no.nav.tms.minesaker.api.sak

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tms.minesaker.api.digisos.DigiSosConsumer
import no.nav.tms.minesaker.api.domain.JournalposterResponse
import no.nav.tms.minesaker.api.domain.Sakstemakode
import no.nav.tms.minesaker.api.saf.SafConsumer
import no.nav.tms.minesaker.api.config.TokendingsExchange
import no.nav.tms.minesaker.api.saf.DokumentStream
import no.nav.tms.minesaker.api.saf.journalposter.JournalposterRequest
import no.nav.tms.minesaker.api.saf.sakstemaer.SakstemaerRequest
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser

class SakService(
    private val safConsumer: SafConsumer,
    private val tokendingsExchange: TokendingsExchange,
    private val digiSosConsumer: DigiSosConsumer,
) {

    private val log = KotlinLogging.logger { }
    private val secureLog = KotlinLogging.logger("secureLog")

    suspend fun hentSakstemaer(user: IdportenUser, representert: String? = null): SakstemaResult = withContext(Dispatchers.IO) {
        if (representert != null) {
            hentSakstemaerForRepresentertFraSaf(user, representert)
        } else {
            val sakstemaerFraSaf = async {
                hentSakstemaerFraSaf(user)
            }
            val sakstemaerFraDigiSos = async {
                hentSakstemaerFraDigiSos(user)
            }
            sakstemaerFraSaf.await() + sakstemaerFraDigiSos.await()
        }
    }

    suspend fun hentJournalposterForSakstema(user: IdportenUser, sakstema: Sakstemakode, representert: String? = null): JournalposterResponse? {
        return if (representert != null) {
            hentJournalposterForRepresentertForSakstema(user, representert, sakstema)
        } else {
            hentJournalposterForBrukerForSakstema(user, sakstema)
        }
    }

    private suspend fun hentSakstemaerFraSaf(user: IdportenUser): SakstemaResult = try {
        val exchangedToken = tokendingsExchange.safToken(user)
        safConsumer.hentSakstemaer(SakstemaerRequest.create(user.ident), exchangedToken)
    } catch (e: Exception) {
        log.error { "Klarte ikke å hente brukers data fra SAF." }
        secureLog.error(e) { "Klarte ikke hente brukers (${user.ident}) data fra SAF." }
        SakstemaResult(errors = listOf(Kildetype.SAF))
    }

    private suspend fun hentSakstemaerForRepresentertFraSaf(user: IdportenUser, representert: String): SakstemaResult {
        val exchangedToken = tokendingsExchange.safToken(user)
        return safConsumer.hentSakstemaer(SakstemaerRequest.create(representert), exchangedToken)
    }

    private suspend fun hentSakstemaerFraDigiSos(user: IdportenUser): SakstemaResult = try {
        val exchangedToken = tokendingsExchange.digisosToken(user)
        digiSosConsumer.hentSakstemaer(exchangedToken)
    } catch (e: Exception) {
        log.error { "Klarte ikke å hente brukers data fra DigiSos." }
        secureLog.error(e) { "Klarte ikke å hente brukers (${user.ident}) data fra DigiSos." }
        SakstemaResult(errors = listOf(Kildetype.DIGISOS))
    }

    private suspend fun hentJournalposterForBrukerForSakstema(user: IdportenUser, sakstema: Sakstemakode): JournalposterResponse? {
        val exchangedToken = tokendingsExchange.safToken(user)
        val journalposterRequest = JournalposterRequest.create(user.ident, sakstema)
        return safConsumer.hentJournalposter(user.ident, journalposterRequest, exchangedToken)
    }


    private suspend fun hentJournalposterForRepresentertForSakstema(user: IdportenUser, representert: String, sakstema: Sakstemakode): JournalposterResponse? {
        val exchangedToken = tokendingsExchange.safToken(user)
        val journalposterRequest = JournalposterRequest.create(representert, sakstema)
        return safConsumer.hentJournalposter(user.ident, journalposterRequest, exchangedToken)
    }

    suspend fun hentDokumentStream(
        user: IdportenUser,
        journapostId: String,
        dokumentinfoId: String,
        receiver: suspend (DokumentStream) -> Unit
    ) {
        log.info { "Henter dokument $dokumentinfoId fra journalposten $journapostId" }
        val exchangedToken = tokendingsExchange.safToken(user)
        safConsumer.hentDokument(journapostId, dokumentinfoId, exchangedToken, receiver)
    }

}
