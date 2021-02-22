package no.nav.personbruker.minesaker.api.saf.dto.`in`.objectmother

import com.expediagroup.graphql.types.GraphQLError
import com.expediagroup.graphql.types.GraphQLResponse
import no.nav.dokument.saf.selvbetjening.generated.dto.HentKonkretSakstemaDTO

object HentKonkretSakstemaDtoResultObjectMother {

    fun giveMeOneResult(): GraphQLResponse<HentKonkretSakstemaDTO.Result> {
        val tema1 = HentKonkretSakstemaDTO.Sakstema("navn1", "kode1")
        val tema2 = HentKonkretSakstemaDTO.Sakstema("navn2", "kode2")
        val tema = listOf(tema1, tema2)
        val dokOver = HentKonkretSakstemaDTO.Dokumentoversikt(tema)
        val data = HentKonkretSakstemaDTO.Result(dokOver)
        return GraphQLResponse(data)
    }

    fun giveMeResponseWithError(): GraphQLResponse<HentKonkretSakstemaDTO.Result> {
        val errors = listOf(GraphQLError("Feilet ved henting av data for bruker."))
        return GraphQLResponse(null, errors = errors)
    }

}
