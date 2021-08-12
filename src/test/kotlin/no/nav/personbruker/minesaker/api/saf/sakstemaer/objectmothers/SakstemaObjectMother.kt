package no.nav.personbruker.minesaker.api.saf.sakstemaer.objectmothers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer

object SakstemaObjectMother {

    fun giveMeOneSakstema(
        navn: String = "navn",
        kode: String = "FOR",
        journalposter: List<HentSakstemaer.Journalpost> = listOf(JournalpostObjectMother.giveMeOneInngaaendeDokument())
    ): HentSakstemaer.Sakstema {
        return HentSakstemaer.Sakstema(navn, kode, journalposter)
    }

    fun giveMeListOfSakstema(): List<HentSakstemaer.Sakstema> {
        return listOf(
            giveMeOneSakstema(),
            giveMeOneSakstema("navn2", "GEN")
        )
    }

}
