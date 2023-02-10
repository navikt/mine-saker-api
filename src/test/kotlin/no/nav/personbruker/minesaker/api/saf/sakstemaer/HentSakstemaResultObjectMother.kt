package no.nav.personbruker.minesaker.api.saf.sakstemaer

import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer
import no.nav.personbruker.minesaker.api.saf.sakstemaer.objectmothers.SakstemaObjectMother
import no.nav.personbruker.minesaker.api.saf.sakstemaer.transformers.GraphQLDokumentoversikt

object HentSakstemaResultObjectMother {
    fun giveMeHentSakstemaResult(): HentSakstemaer.Result {
        val temaer = SakstemaObjectMother.giveMeListOfSakstema()
        val dokumentoversikt = GraphQLDokumentoversikt(temaer)
        return HentSakstemaer.Result(dokumentoversikt)
    }

    fun giveMeHentSakstemaResultMedUfullstendigeData(): HentSakstemaer.Result {
        val sakstemaUtenKodeSatt = SakstemaObjectMother.giveMeOneSakstema(kode = "UGYLDIG_VERDI")
        val dokumentoversikt = GraphQLDokumentoversikt(listOf(sakstemaUtenKodeSatt))
        return HentSakstemaer.Result(dokumentoversikt)
    }
}
