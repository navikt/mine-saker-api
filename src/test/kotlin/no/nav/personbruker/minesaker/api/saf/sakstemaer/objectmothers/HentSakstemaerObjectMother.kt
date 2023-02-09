package no.nav.personbruker.minesaker.api.saf.sakstemaer.objectmothers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer
import no.nav.personbruker.minesaker.api.saf.common.GraphQLError
import no.nav.personbruker.minesaker.api.saf.common.GraphQLResponse
import no.nav.personbruker.minesaker.api.saf.sakstemaer.HentSakstemaResultObjectMother

object HentSakstemaerObjectMother {

    fun giveMeOneResult(): GraphQLResponse<HentSakstemaer.Result> {
        val data = HentSakstemaResultObjectMother.giveMeHentSakstemaResult()
        return GraphQLResponse(data)
    }

    fun giveMeResponseWithError(data: HentSakstemaer.Result? = null): GraphQLResponse<HentSakstemaer.Result> {
        val error = GraphQLError("Feilet ved henting av data for bruker.")

        return GraphQLResponse(data, listOf(error))
    }

    fun giveMeResponseWithDataAndError(): GraphQLResponse<HentSakstemaer.Result> {
        val data = HentSakstemaResultObjectMother.giveMeHentSakstemaResult()
        return giveMeResponseWithError(data)
    }

}
