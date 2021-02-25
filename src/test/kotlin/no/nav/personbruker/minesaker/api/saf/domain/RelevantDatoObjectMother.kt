package no.nav.personbruker.minesaker.api.saf.domain

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter

object RelevantDatoObjectMother {

    fun giveMeOneOfEachEkspederRegistertAndOpprettet(): List<HentJournalposter.RelevantDato> {
        return listOf(
            giveMeDatoForInngaaendeDokument(),
            giveMeDatoForUtgaaendeDokument(),
            giveMeDatoForNotat()
        )
    }

    fun giveMeDatoForUtgaaendeDokument(): HentJournalposter.RelevantDato {
        return HentJournalposter.RelevantDato("2018-01-01T12:00:00", HentJournalposter.Datotype.DATO_EKSPEDERT)
    }

    fun giveMeDatoForInngaaendeDokument(): HentJournalposter.RelevantDato {
        return HentJournalposter.RelevantDato("2018-02-02T12:00:00", HentJournalposter.Datotype.DATO_REGISTRERT)
    }

    fun giveMeDatoForNotat(): HentJournalposter.RelevantDato {
        return HentJournalposter.RelevantDato("2018-03-03T12:00:00", HentJournalposter.Datotype.DATO_OPPRETTET)
    }

}
