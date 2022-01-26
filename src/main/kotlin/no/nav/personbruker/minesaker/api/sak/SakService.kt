package no.nav.personbruker.minesaker.api.sak

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import no.nav.personbruker.minesaker.api.common.exception.InvalidRequestException
import no.nav.personbruker.minesaker.api.digisos.DigiSosConsumer
import no.nav.personbruker.minesaker.api.digisos.DigiSosTokendings
import no.nav.personbruker.minesaker.api.domain.*
import no.nav.personbruker.minesaker.api.saf.SafConsumer
import no.nav.personbruker.minesaker.api.saf.SafTokendings
import no.nav.personbruker.minesaker.api.saf.journalposter.JournalposterRequest
import no.nav.personbruker.minesaker.api.saf.sakstemaer.SakstemaerRequest
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser

class SakService(
    private val safConsumer: SafConsumer,
    private val safTokendings: SafTokendings,
    private val digiSosConsumer: DigiSosConsumer,
    private val digiSosTokendings: DigiSosTokendings
) {

    suspend fun hentSakstemaer(user: AuthenticatedUser): SakstemaResult = withContext(Dispatchers.IO) {
        val sakstemaerFraSaf = async {
            hentSakstemaerFraSaf(user)
        }
        val sakstemaerFraDigiSos = async {
            hentSakstemaerFraDigiSos(user)
        }
        sakstemaerFraSaf.await() + sakstemaerFraDigiSos.await()
    }

    suspend fun hentSakstemaerFraSaf(user: AuthenticatedUser): SakstemaResult {
        val exchangedToken = safTokendings.exchangeToken(user)
        val fodselsnummer = Fodselsnummer(user.ident)
        val sakstemaerRequest = SakstemaerRequest.create(fodselsnummer)
        return safConsumer.hentSakstemaer(sakstemaerRequest, exchangedToken)
    }

    suspend fun hentSakstemaerFraDigiSos(user: AuthenticatedUser): SakstemaResult {
        val exchangedToken = digiSosTokendings.exchangeToken(user)
        return digiSosConsumer.hentSakstemaer(exchangedToken)
    }

    suspend fun hentJournalposterForSakstema(user: IdportenUser, sakstema: Sakstemakode): List<Sakstema> {
        val exchangedToken = safTokendings.exchangeToken(user)
        val fodselsnummer = Fodselsnummer(user.ident)
        val journalposterRequest = JournalposterRequest.create(fodselsnummer, sakstema)
        return safConsumer.hentJournalposter(fodselsnummer, journalposterRequest, exchangedToken)
    }

    suspend fun hentDokument(
        user: IdportenUser,
        journapostId: JournalpostId,
        dokumentinfoId: DokumentInfoId
    ): ByteArray {
        if(dokumentinfoId.value == "-") {
            throw InvalidRequestException("Det ble forsøkt hentet et dokument som bruker ikke har tilgang til")
        }
        val exchangedToken = safTokendings.exchangeToken(user)
        return safConsumer.hentDokument(journapostId, dokumentinfoId, exchangedToken)
    }

}
