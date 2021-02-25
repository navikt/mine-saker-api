package no.nav.personbruker.minesaker.api.saf

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer
import no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers.SakstemaObjectMother

object ResultObjectMother {

    fun giveMeHentSakstemaResult(): HentSakstemaer.Result {
        val tema = no.nav.personbruker.minesaker.api.saf.sakstemaer.SakstemaObjectMother.giveMeListOfSakstema()
        val dokumentoversikt = HentSakstemaer.Dokumentoversikt(tema)
        return HentSakstemaer.Result(dokumentoversikt)
    }

    fun giveMeHentJournalposterResult(): HentJournalposter.Result {
        val tema = SakstemaObjectMother.giveMeListOfSakstemaer()
        val dokumentoversikt = HentJournalposter.Dokumentoversikt(tema)
        return HentJournalposter.Result(dokumentoversikt)
    }

}
