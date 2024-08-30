package no.nav.tms.minesaker.api.saf.sakstemaer

import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer

object HentSakstemaResultTestData {
    fun result(): HentSakstemaer.Result {
        val temaer = listOf(
            SakstemaTestData.sakstema(),
            SakstemaTestData.sakstema("navn2", "GEN")
        )
        val dokumentoversikt = SafDokumentoversikt(temaer)
        return HentSakstemaer.Result(dokumentoversikt)
    }

    fun medUfullstendigeData(): HentSakstemaer.Result {
        val sakstemaUtenKodeSatt = SakstemaTestData.sakstema(kode = "UGYLDIG_VERDI")
        val dokumentoversikt = SafDokumentoversikt(listOf(sakstemaUtenKodeSatt))
        return HentSakstemaer.Result(dokumentoversikt)
    }
}
