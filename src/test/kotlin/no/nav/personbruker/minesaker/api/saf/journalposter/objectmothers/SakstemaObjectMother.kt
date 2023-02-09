package no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers

import no.nav.personbruker.minesaker.api.saf.journalposter.transformers.GraphQLSakstema

object SakstemaObjectMother {

    fun giveMeListOfSakstemaer(): List<GraphQLSakstema> {
        return listOf(
            giveMeSakstemaWithUtgaaendeDokument(),
            giveMeSakstemaWithInngaaendeDokument()
        )
    }

    fun giveMeSakstemaWithUtgaaendeDokument(navn: String = "navn1", kode: String = "AAP") =
        GraphQLSakstema(navn, kode, listOf(JournalpostObjectMother.giveMeOneInngaaendeDokument()))

    fun giveMeSakstemaWithInngaaendeDokument(navn: String = "navn2", kode: String = "KON") =
        GraphQLSakstema(navn, kode, listOf(JournalpostObjectMother.giveMeOneUtgaaendeDokument()))

}
