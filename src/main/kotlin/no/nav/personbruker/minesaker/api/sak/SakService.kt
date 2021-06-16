package no.nav.personbruker.minesaker.api.sak

import no.nav.personbruker.minesaker.api.domain.*
import no.nav.personbruker.minesaker.api.saf.SafConsumer
import no.nav.personbruker.minesaker.api.saf.journalposter.JournalposterRequest
import no.nav.personbruker.minesaker.api.saf.sakstemaer.SakstemaerRequest
import no.nav.personbruker.minesaker.api.saf.SafTokendingsService
import no.nav.tms.token.support.idporten.user.IdportenUser

class SakService(
    private val safConsumer: SafConsumer,
    private val safTokendings: SafTokendingsService
) {

    suspend fun hentSakstemaer(user: IdportenUser): List<ForenkletSakstema> {
        val exchangedToken = safTokendings.exchangeToken(user)
        val fodselsnummer = Fodselsnummer(user.ident)
        val sakstemaerRequest = SakstemaerRequest.create(fodselsnummer)
        return safConsumer.hentSakstemaer(sakstemaerRequest, exchangedToken)
    }

    suspend fun hentJournalposterForSakstema(user: IdportenUser, sakstema: Sakstemakode): List<Sakstema> {
        val exchangedToken = safTokendings.exchangeToken(user)
        val fodselsnummer = Fodselsnummer(user.ident)
        val journalposterRequest = JournalposterRequest.create(fodselsnummer, sakstema)
        return safConsumer.hentJournalposter(fodselsnummer, journalposterRequest, exchangedToken)
    }

    suspend fun hentDokument(user: IdportenUser, journapostId : JournalpostId, dokumentinfoId : DokumentInfoId): ByteArray {
        val exchangedToken = safTokendings.exchangeToken(user)
        return safConsumer.hentDokument(journapostId, dokumentinfoId, exchangedToken)
    }

}
