package no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers

import no.nav.personbruker.minesaker.api.saf.journalposter.transformers.GraphQLDatotype
import no.nav.personbruker.minesaker.api.saf.journalposter.transformers.GraphQLRelevantDato

object RelevantDatoObjectMother {

    fun giveMeOneOfEachEkspederRegistertAndOpprettet(): List<GraphQLRelevantDato> {
        return listOf(
            giveMeDatoForInngaaendeDokument(),
            giveMeDatoForUtgaaendeDokument(),
            giveMeDatoForNotat()
        )
    }

    fun giveMeDatoForUtgaaendeDokument(): GraphQLRelevantDato {
        return GraphQLRelevantDato("2018-01-01T12:00:00", GraphQLDatotype.DATO_EKSPEDERT)
    }

    fun giveMeDatoForInngaaendeDokument(): GraphQLRelevantDato {
        return GraphQLRelevantDato("2018-02-02T12:00:00", GraphQLDatotype.DATO_REGISTRERT)
    }

    fun giveMeDatoForNotat(): GraphQLRelevantDato {
        return GraphQLRelevantDato("2018-03-03T12:00:00", GraphQLDatotype.DATO_OPPRETTET)
    }

}
