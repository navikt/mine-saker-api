package no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter

object SakstemaObjectMother {

    fun giveMeListOfSakstemaer(): List<HentJournalposter.Sakstema> {
        return listOf(
            giveMeSakstemaWithUtgaaendeDokument(),
            giveMeSakstemaWithInngaaendeDokument()
        )
    }

    fun giveMeSakstemaWithUtgaaendeDokument(navn: String? = "navn1", kode: String? = "AAP") =
        HentJournalposter.Sakstema(navn, kode, listOf(JournalpostObjectMother.giveMeOneInngaaendeDokument()))

    fun giveMeSakstemaWithInngaaendeDokument(navn: String? = "navn2", kode: String? = "KON") =
        HentJournalposter.Sakstema(navn, kode, listOf(JournalpostObjectMother.giveMeOneUtgaaendeDokument()))

}
