package no.nav.personbruker.minesaker.api.saf

import com.expediagroup.graphql.types.GraphQLResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.dokument.saf.selvbetjening.generated.dto.HentKonkretSakstemaDTO
import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakerDTO
import no.nav.personbruker.minesaker.api.common.exception.SafException
import no.nav.personbruker.minesaker.api.saf.dto.out.Sakstema
import no.nav.personbruker.minesaker.api.saf.queries.HentJournalposter
import no.nav.personbruker.minesaker.api.saf.queries.HentSakstema
import java.net.URL

class SafConsumer(
    private val httpClient: HttpClient,
    private val sakerTransformer: HentSakerDtoTransformer = HentSakerDtoTransformer,
    private val konkretTransformer: HentKonkretSakstemaDtoTransformer = HentKonkretSakstemaDtoTransformer,
    private val safEndpoint: URL
) {

    suspend fun hentSakstemaer(request: HentSakstema): List<Sakstema> {
        val responseDto: GraphQLResponse<HentSakerDTO.Result> = sendQuery(request)
        val data: HentSakerDTO.Result = responseDto.data ?: throw noDataWithContext(responseDto)
        return sakerTransformer.toInternal(data)
    }

    suspend fun hentJournalposter(request: HentJournalposter): List<Sakstema> {
        val responseDto = sendQuery<GraphQLResponse<HentKonkretSakstemaDTO.Result>>(request)
        val data: HentKonkretSakstemaDTO.Result =
            responseDto.data ?: throw noDataWithContext(responseDto)
        return konkretTransformer.toInternal(data)
    }

    private fun noDataWithContext(responseDto: GraphQLResponse<*>) =
        SafException("Ingen data i resultatet fra SAF.")
            .addContext("response", responseDto)

    private suspend inline fun <reified T> sendQuery(request: GraphQLRequest): T {
        return try {
            withContext(Dispatchers.IO) {
                httpClient.post {
                    url(safEndpoint)
                    method = HttpMethod.Post
                    contentType(ContentType.Application.Json)
                    accept(ContentType.Application.Json)
                    body = request
                }
            }

        } catch (e: Exception) {
            val internalException = SafException("Klarte ikke å utføre spørring mot SAF", e)
            internalException.addContext("query", request.query)
            internalException.addContext("variables", request.variables)
            throw internalException
        }

    }

}
