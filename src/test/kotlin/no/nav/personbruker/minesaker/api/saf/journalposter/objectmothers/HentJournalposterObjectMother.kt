package no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers

import com.expediagroup.graphql.types.GraphQLError
import com.expediagroup.graphql.types.GraphQLResponse
import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.saf.ResultObjectMother

object HentJournalposterResultObjectMother {

    fun giveMeOneResult(): GraphQLResponse<HentJournalposter.Result> {
        val data = ResultObjectMother.giveMeHentJournalposterResult()
        return GraphQLResponse(data)
    }

    fun giveMeResponseWithError(): GraphQLResponse<HentJournalposter.Result> {
        val errors = listOf(GraphQLError("Feilet ved henting av data for bruker."))
        return GraphQLResponse(null, errors = errors)
    }

}
