package no.nav.personbruker.minesaker.api.saf.dto.`in`.objectmother

import com.expediagroup.graphql.types.GraphQLError
import com.expediagroup.graphql.types.GraphQLResponse
import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakerDTO

object HentSakerDtoObjectMother {

    fun giveMeOneResult(): GraphQLResponse<HentSakerDTO.Result> {
        val tema = listOf(HentSakerDTO.Sakstema("tema", "kode"))
        val docOver = HentSakerDTO.Dokumentoversikt(tema)
        val data = HentSakerDTO.Result(docOver)
        return GraphQLResponse(data)
    }

    fun giveMeResponseWithError() : GraphQLResponse<HentSakerDTO.Result> {
        val errors = listOf(GraphQLError("Feilet ved henting av data for bruker."))
        return GraphQLResponse(null, errors = errors)
    }

}
