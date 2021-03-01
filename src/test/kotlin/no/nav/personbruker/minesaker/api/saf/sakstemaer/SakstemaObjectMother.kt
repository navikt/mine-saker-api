package no.nav.personbruker.minesaker.api.saf.sakstemaer

import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer

object SakstemaObjectMother {

    fun giveMeOneSakstema(): HentSakstemaer.Sakstema {
        return HentSakstemaer.Sakstema("navn", "kode")
    }

    fun giveMeListOfSakstema(): List<HentSakstemaer.Sakstema> {
        return listOf(
            giveMeOneSakstema(),
            HentSakstemaer.Sakstema("navn2", "kode2")
        )
    }

}
