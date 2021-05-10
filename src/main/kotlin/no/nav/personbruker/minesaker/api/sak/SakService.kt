package no.nav.personbruker.minesaker.api.sak

import no.nav.personbruker.minesaker.api.domain.*
import no.nav.personbruker.minesaker.api.saf.SafConsumer
import no.nav.personbruker.minesaker.api.saf.journalposter.JournalposterRequest
import no.nav.personbruker.minesaker.api.saf.sakstemaer.SakstemaerRequest
import no.nav.personbruker.minesaker.api.tokenx.AccessToken
import no.nav.personbruker.minesaker.api.tokenx.TokendingsServiceWrapper
import no.nav.tms.token.support.idporten.user.IdportenUser

class SakService(
    private val safConsumer: SafConsumer,
    private val tokendingsWrapper: TokendingsServiceWrapper
) {

    suspend fun hentSakstemaer(user: IdportenUser): List<ForenkletSakstema> {
        val exchangedToken = exchangeToken(user)
        val fodselsnummer = Fodselsnummer(user.ident)
        val sakstemaerRequest = SakstemaerRequest.create(fodselsnummer)
        return safConsumer.hentSakstemaer(sakstemaerRequest, exchangedToken)
    }

    suspend fun hentJournalposterForSakstema(user: IdportenUser, sakstema: Sakstemakode): List<Sakstema> {
        val exchangedToken = exchangeToken(user)
        val fodselsnummer = Fodselsnummer(user.ident)
        val journalposterRequest = JournalposterRequest.create(fodselsnummer, sakstema)
        return safConsumer.hentJournalposter(fodselsnummer, journalposterRequest, exchangedToken)
    }

    suspend fun hentDokument(user: IdportenUser, journapostId : JournalpostId, dokumentinfoId : DokumentInfoId): ByteArray {
        val exchangedToken = exchangeToken(user)
        return safConsumer.hentDokument(journapostId, dokumentinfoId, exchangedToken)
    }

    private suspend fun exchangeToken(user: IdportenUser): AccessToken {
        return tokendingsWrapper.exchangeTokenForSafSelvbetjening(user.tokenString)
    }

}
