package no.nav.personbruker.minesaker.api.saf.sakstemaer.objectmothers

import no.nav.personbruker.minesaker.api.saf.sakstemaer.transformers.GraphQLRelevantDato

object RelevantDatoObjectMother {

    fun giveMeOneOfEachEkspederRegistertAndOpprettet(): List<GraphQLRelevantDato> {
        return listOf(
            giveMeDatoForInngaaendeDokument(),
            giveMeDatoForUtgaaendeDokument(),
            giveMeDatoForNotat()
        )
    }

    fun giveMeDatoForUtgaaendeDokument(): GraphQLRelevantDato {
        return GraphQLRelevantDato("2018-06-01T12:00:00")
    }

    fun giveMeDatoForInngaaendeDokument(): GraphQLRelevantDato {
        return GraphQLRelevantDato("2018-04-02T12:00:00")
    }

    fun giveMeDatoForNotat(): GraphQLRelevantDato {
        return GraphQLRelevantDato("2018-05-03T12:00:00")
    }

}
