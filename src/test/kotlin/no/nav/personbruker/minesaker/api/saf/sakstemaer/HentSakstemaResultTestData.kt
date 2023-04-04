package no.nav.personbruker.minesaker.api.saf.sakstemaer

import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer
import no.nav.personbruker.minesaker.api.saf.sakstemaer.transformers.GraphQLDokumentoversikt

object HentSakstemaResultTestData {
    fun result(): HentSakstemaer.Result {
        val temaer = listOf(
            SakstemaTestData.sakstema(),
            SakstemaTestData.sakstema("navn2", "GEN")
        )
        val dokumentoversikt = GraphQLDokumentoversikt(temaer)
        return HentSakstemaer.Result(dokumentoversikt)
    }

    fun medUfullstendigeData(): HentSakstemaer.Result {
        val sakstemaUtenKodeSatt = SakstemaTestData.sakstema(kode = "UGYLDIG_VERDI")
        val dokumentoversikt = GraphQLDokumentoversikt(listOf(sakstemaUtenKodeSatt))
        return HentSakstemaer.Result(dokumentoversikt)
    }
}
