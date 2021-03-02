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
import no.nav.personbruker.minesaker.api.saf.domain.Sakstema
import no.nav.personbruker.minesaker.api.saf.journalposter.JournalposterRequest
import no.nav.personbruker.minesaker.api.saf.sakstemaer.SakstemaerRequest
import java.net.URL

class SafConsumer(
    private val httpClient: HttpClient,
    private val safEndpoint: URL
) {

    suspend fun hentSakstemaer(request: SakstemaerRequest): List<Sakstema> {
        val responseDto: GraphQLResponse<HentSakstemaer.Result> = sendQuery(request)
        val data: HentSakstemaer.Result = responseDto.data ?: throw noDataWithContext(responseDto)
        return data.toInternal()
    }

    suspend fun hentJournalposter(request: JournalposterRequest): List<Sakstema> {
        val responseDto = sendQuery<GraphQLResponse<HentJournalposter.Result>>(request)
        val data: HentJournalposter.Result = responseDto.data ?: throw noDataWithContext(responseDto)
        return data.toInternal()
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
