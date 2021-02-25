package no.nav.personbruker.minesaker.api.saf.sakstemaer

import com.expediagroup.graphql.types.GraphQLError
import com.expediagroup.graphql.types.GraphQLResponse
import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer

object HentSakstemaerObjectMother {

    fun giveMeOneResult(): GraphQLResponse<HentSakstemaer.Result> {
        val tema = listOf(HentSakstemaer.Sakstema("tema", "kode"))
        val docOver = HentSakstemaer.Dokumentoversikt(tema)
        val data = HentSakstemaer.Result(docOver)
        return GraphQLResponse(data)
    }

    fun giveMeResponseWithError() : GraphQLResponse<HentSakstemaer.Result> {
        val errors = listOf(GraphQLError("Feilet ved henting av data for bruker."))
        return GraphQLResponse(null, errors = errors)
    }

}
