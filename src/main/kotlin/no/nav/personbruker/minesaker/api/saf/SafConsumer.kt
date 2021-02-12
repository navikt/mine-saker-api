package no.nav.personbruker.minesaker.api.saf

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.personbruker.minesaker.api.common.exception.SafException
import no.nav.personbruker.minesaker.api.saf.dto.`in`.SafResultWrapper
import no.nav.personbruker.minesaker.api.saf.dto.out.Sakstema
import no.nav.personbruker.minesaker.api.saf.queries.HentKonkretSakstema
import no.nav.personbruker.minesaker.api.saf.queries.HentSaker
import java.net.URL

class SafConsumer(
    private val httpClient: HttpClient,
    private val transformer: SakstemaTransformer = SakstemaTransformer,
    private val safEndpoint: URL
) {

    suspend fun hentSaker(request: HentSaker): List<Sakstema> {
        val responseDto = sendQuery<SafResultWrapper>(request)
        return transformer.toInternal(responseDto.data.dokumentoversiktSelvbetjening.tema)
    }

    suspend fun hentKonkretSakstema(request: HentKonkretSakstema): List<Sakstema> {
        val responseDto = sendQuery<SafResultWrapper>(request)
        return transformer.toInternal(responseDto.data.dokumentoversiktSelvbetjening.tema)
    }

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

        } catch (e : Exception) {
            val internalException = SafException("Klarte ikke å utføre spørring mot SAF", e)
            internalException.addContext("query", request.query)
            internalException.addContext("variables", request.variables)
            throw internalException
        }

    }

}
