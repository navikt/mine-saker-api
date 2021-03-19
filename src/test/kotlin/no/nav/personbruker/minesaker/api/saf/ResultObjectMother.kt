package no.nav.personbruker.minesaker.api.saf

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer
import no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers.SakstemaObjectMother

object ResultObjectMother {

    private const val defaultResultStatusCode = "ok"

    fun giveMeHentSakstemaResult(): HentSakstemaer.Result {
        val temaer = no.nav.personbruker.minesaker.api.saf.sakstemaer.SakstemaObjectMother.giveMeListOfSakstema()
        val dokumentoversikt = HentSakstemaer.Dokumentoversikt(defaultResultStatusCode, temaer)
        return HentSakstemaer.Result(dokumentoversikt)
    }

    fun giveMeHentSakstemaResultMedUfullstendigeData(): HentSakstemaer.Result {
        val sakstemaUtenKodeSatt =
            no.nav.personbruker.minesaker.api.saf.sakstemaer.SakstemaObjectMother.giveMeOneSakstema(kode = null)
        val dokumentoversikt = HentSakstemaer.Dokumentoversikt(defaultResultStatusCode, listOf(sakstemaUtenKodeSatt))
        return HentSakstemaer.Result(dokumentoversikt)
    }

    fun giveMeHentJournalposterResult(): HentJournalposter.Result {
        val temaer = SakstemaObjectMother.giveMeListOfSakstemaer()
        val dokumentoversikt = HentJournalposter.Dokumentoversikt(defaultResultStatusCode, temaer)
        return HentJournalposter.Result(dokumentoversikt)
    }

    fun giveMeHentJournalposterResultMedUfullstendigeData(): HentJournalposter.Result {
        val sakstemaUtenKodeSatt = SakstemaObjectMother.giveMeSakstemaWithUtgaaendeDokument(kode = null)
        val responseCodeIndicatingError = "not_found"
        val dokumentoversikt = HentJournalposter.Dokumentoversikt(responseCodeIndicatingError, listOf(sakstemaUtenKodeSatt))
        return HentJournalposter.Result(dokumentoversikt)
    }

}
