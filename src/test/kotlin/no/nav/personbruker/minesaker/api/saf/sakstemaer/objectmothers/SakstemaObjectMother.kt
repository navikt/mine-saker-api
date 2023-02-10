package no.nav.personbruker.minesaker.api.saf.sakstemaer.objectmothers

import no.nav.personbruker.minesaker.api.saf.sakstemaer.transformers.GraphQLJournalpost
import no.nav.personbruker.minesaker.api.saf.sakstemaer.transformers.GraphQLSakstema

object SakstemaObjectMother {

    fun giveMeOneSakstema(
        navn: String = "navn",
        kode: String = "FOR",
        journalposter: List<GraphQLJournalpost> = listOf(JournalpostObjectMother.giveMeOneInngaaendeDokument())
    ): GraphQLSakstema {
        return GraphQLSakstema(navn, kode, journalposter)
    }

    fun giveMeListOfSakstema(): List<GraphQLSakstema> {
        return listOf(
            giveMeOneSakstema(),
            giveMeOneSakstema("navn2", "GEN")
        )
    }

}
