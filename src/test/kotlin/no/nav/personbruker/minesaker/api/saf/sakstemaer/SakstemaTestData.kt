package no.nav.personbruker.minesaker.api.saf.sakstemaer

import no.nav.personbruker.minesaker.api.saf.sakstemaer.transformers.GraphQLJournalpost
import no.nav.personbruker.minesaker.api.saf.sakstemaer.transformers.GraphQLRelevantDato
import no.nav.personbruker.minesaker.api.saf.sakstemaer.transformers.GraphQLSakstema

object SakstemaTestData {

    fun sakstema(
        navn: String = "navn",
        kode: String = "FOR",
        journalposter: List<GraphQLJournalpost> = listOf(inngaaendeDokument())
    ): GraphQLSakstema {
        return GraphQLSakstema(navn, kode, journalposter)
    }

    fun inngaaendeDokument(
        relevanteDatoer: List<GraphQLRelevantDato?> = listOf(
            datoForInngaaendeDokument(),
            datoForUtgaaendeDokument()
        ),
    ) = GraphQLJournalpost(relevanteDatoer)

    fun datoForUtgaaendeDokument(): GraphQLRelevantDato {
        return GraphQLRelevantDato("2018-06-01T12:00:00")
    }

    fun datoForInngaaendeDokument(): GraphQLRelevantDato {
        return GraphQLRelevantDato("2018-04-02T12:00:00")
    }

    fun datoForNotat(): GraphQLRelevantDato {
        return GraphQLRelevantDato("2018-05-03T12:00:00")
    }

}
