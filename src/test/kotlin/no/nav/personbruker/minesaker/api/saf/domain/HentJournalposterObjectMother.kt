package no.nav.personbruker.minesaker.api.saf.domain

import com.expediagroup.graphql.types.GraphQLError
import com.expediagroup.graphql.types.GraphQLResponse
import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter

object HentJournalposterResultObjectMother {

    fun giveMeOneResult(): GraphQLResponse<HentJournalposter.Result> {
        val tema1 = HentJournalposter.Sakstema("navn1", "kode1")
        val tema2 = HentJournalposter.Sakstema("navn2", "kode2")
        val tema = listOf(tema1, tema2)
        val dokOver = HentJournalposter.Dokumentoversikt(tema)
        val data = HentJournalposter.Result(dokOver)
        return GraphQLResponse(data)
    }

    fun giveMeResponseWithError(): GraphQLResponse<HentJournalposter.Result> {
        val errors = listOf(GraphQLError("Feilet ved henting av data for bruker."))
        return GraphQLResponse(null, errors = errors)
    }

}
