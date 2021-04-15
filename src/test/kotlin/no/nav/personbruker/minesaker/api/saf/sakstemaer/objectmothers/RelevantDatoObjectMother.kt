package no.nav.personbruker.minesaker.api.saf.sakstemaer.objectmothers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer

object RelevantDatoObjectMother {

    fun giveMeOneOfEachEkspederRegistertAndOpprettet(): List<HentSakstemaer.RelevantDato> {
        return listOf(
            giveMeDatoForInngaaendeDokument(),
            giveMeDatoForUtgaaendeDokument(),
            giveMeDatoForNotat()
        )
    }

    fun giveMeDatoForUtgaaendeDokument(): HentSakstemaer.RelevantDato {
        return HentSakstemaer.RelevantDato("2018-06-01T12:00:00")
    }

    fun giveMeDatoForInngaaendeDokument(): HentSakstemaer.RelevantDato {
        return HentSakstemaer.RelevantDato("2018-04-02T12:00:00")
    }

    fun giveMeDatoForNotat(): HentSakstemaer.RelevantDato {
        return HentSakstemaer.RelevantDato("2018-05-03T12:00:00")
    }

}
