package no.nav.tms.minesaker.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tms.minesaker.api.digisos.DigiSosConsumer
import no.nav.tms.minesaker.api.saf.sakstemaer.Sakstemakode
import no.nav.tms.minesaker.api.saf.SafConsumer
import no.nav.tms.minesaker.api.setup.TokendingsExchange
import no.nav.tms.minesaker.api.saf.journalposter.v2.HentJournalposterResponseV2
import no.nav.tms.minesaker.api.saf.DokumentStream
import no.nav.tms.minesaker.api.saf.journalposter.v2.HentJournalposterV2Request
import no.nav.tms.minesaker.api.saf.journalposter.v1.JournalposterRequest
import no.nav.tms.minesaker.api.saf.journalposter.v1.JournalposterResponse
import no.nav.tms.minesaker.api.saf.journalposter.v2.AlleJournalposterRequest
import no.nav.tms.minesaker.api.saf.journalposter.v2.JournalpostV2
import no.nav.tms.minesaker.api.saf.sakstemaer.Kildetype
import no.nav.tms.minesaker.api.saf.sakstemaer.SakstemaResult
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
        SakstemaResult.withErrors(errors = listOf(Kildetype.SAF))
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
        SakstemaResult.withErrors(errors = listOf(Kildetype.DIGISOS))
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

    suspend fun hentJournalposterV2(user: IdportenUser, representert: String?, sakstema: Sakstemakode): HentJournalposterResponseV2? {
        return if (representert != null) {
            hentJournalposterForRepresentertV2(user, representert, sakstema)
        } else {
            hentJournalposterForBrukerV2(user, sakstema)
        }
    }

    suspend fun hentJournalposterForBrukerV2(user: IdportenUser, sakstema: Sakstemakode): HentJournalposterResponseV2? {
        log.info { "Henter alle journalposter for bruker fra SAF" }

        return safConsumer.hentJournalposterV2(
            request = HentJournalposterV2Request.create(user.ident, sakstema),
            accessToken = tokendingsExchange.safToken(user)
        )
    }

    suspend fun hentJournalposterForRepresentertV2(user: IdportenUser, representert: String, sakstema: Sakstemakode): HentJournalposterResponseV2? {
        log.info { "Henter alle journalposter for representert fra SAF" }

        return safConsumer.hentJournalposterV2(
            request = HentJournalposterV2Request.create(representert, sakstema),
            accessToken = tokendingsExchange.safToken(user)
        )
    }

    suspend fun alleJournalposter(user: IdportenUser, representert: String?): List<JournalpostV2> =
        if (representert != null) {
            log.info { "Henter alle journalposter for representert fra SAF" }

            safConsumer.alleJournalposter(
                request = AlleJournalposterRequest.create(representert),
                accessToken = tokendingsExchange.safToken(user)
            )
        } else {
            log.info { "Henter alle journalposter for bruker fra SAF" }

            safConsumer.alleJournalposter(
                request = AlleJournalposterRequest.create(user.ident),
                accessToken = tokendingsExchange.safToken(user)
            )
        }

}
