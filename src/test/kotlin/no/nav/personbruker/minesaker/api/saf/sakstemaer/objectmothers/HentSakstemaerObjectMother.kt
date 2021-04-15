package no.nav.personbruker.minesaker.api.saf.sakstemaer.objectmothers

import com.expediagroup.graphql.types.GraphQLError
import com.expediagroup.graphql.types.GraphQLResponse
import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer
import no.nav.personbruker.minesaker.api.saf.ResultObjectMother

object HentSakstemaerObjectMother {

    fun giveMeOneResult(): GraphQLResponse<HentSakstemaer.Result> {
        val data = ResultObjectMother.giveMeHentSakstemaResult()
        return GraphQLResponse(data)
    }

    fun giveMeResponseWithError(data: HentSakstemaer.Result? = null): GraphQLResponse<HentSakstemaer.Result> {
        val errors = listOf(GraphQLError("Feilet ved henting av data for bruker."))
        return GraphQLResponse(data, errors = errors)
    }

    fun giveMeResponseWithDataAndError(): GraphQLResponse<HentSakstemaer.Result> {
        val data = ResultObjectMother.giveMeHentSakstemaResult()
        return giveMeResponseWithError(data)
    }

}
