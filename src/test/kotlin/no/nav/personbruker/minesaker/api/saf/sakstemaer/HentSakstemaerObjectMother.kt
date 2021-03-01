package no.nav.personbruker.minesaker.api.saf.sakstemaer

import com.expediagroup.graphql.types.GraphQLError
import com.expediagroup.graphql.types.GraphQLResponse
import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer
import no.nav.personbruker.minesaker.api.saf.ResultObjectMother

object HentSakstemaerObjectMother {

    fun giveMeOneResult(): GraphQLResponse<HentSakstemaer.Result> {
        val data = ResultObjectMother.giveMeHentSakstemaResult()
        return GraphQLResponse(data)
    }

    fun giveMeResponseWithError() : GraphQLResponse<HentSakstemaer.Result> {
        val errors = listOf(GraphQLError("Feilet ved henting av data for bruker."))
        return GraphQLResponse(null, errors = errors)
    }

}
