package no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter

object SakstemaObjectMother {

    fun giveMeListOfSakstemaer(): List<HentJournalposter.Sakstema> {
        return listOf(
            giveMeSakstemaWithUtgaaendeDokument(),
            giveMeSakstemaWithInngaaendeDokument()
        )
    }

    fun giveMeSakstemaWithUtgaaendeDokument() =
        HentJournalposter.Sakstema("navn1", "kode1", listOf(JournalpostObjectMother.giveMeOneInngaaendeDokument()))

    fun giveMeSakstemaWithInngaaendeDokument() =
        HentJournalposter.Sakstema("navn2", "kode2", listOf(JournalpostObjectMother.giveMeOneUtgaaendeDokument()))

}
