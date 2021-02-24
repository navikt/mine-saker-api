package no.nav.personbruker.minesaker.api.saf

import com.expediagroup.graphql.types.GraphQLResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer
import no.nav.personbruker.minesaker.api.common.exception.SafException
import no.nav.personbruker.minesaker.api.saf.domain.MinimaltSakstema
import no.nav.personbruker.minesaker.api.saf.requests.JournalposterRequest
import no.nav.personbruker.minesaker.api.saf.requests.SakstemaerRequest
import no.nav.personbruker.minesaker.api.saf.transformers.HentJournalposterTransformer
import no.nav.personbruker.minesaker.api.saf.transformers.HentSakstemaerTransformer
import java.net.URL

class SafConsumer(
    private val httpClient: HttpClient,
    private val sakerTransformer: HentSakstemaerTransformer = HentSakstemaerTransformer,
    private val konkretTransformer: HentJournalposterTransformer = HentJournalposterTransformer,
    private val safEndpoint: URL
) {

    suspend fun hentSakstemaer(request: SakstemaerRequest): List<MinimaltSakstema> {
        val responseDto: GraphQLResponse<HentSakstemaer.Result> = sendQuery(request)
        val data: HentSakstemaer.Result = responseDto.data ?: throw noDataWithContext(responseDto)
        return sakerTransformer.toInternal(data)
    }

    suspend fun hentJournalposter(request: JournalposterRequest): List<MinimaltSakstema> {
        val responseDto = sendQuery<GraphQLResponse<HentJournalposter.Result>>(request)
        val data: HentJournalposter.Result =
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
