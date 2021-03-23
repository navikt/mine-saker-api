package no.nav.personbruker.minesaker.api.saf

import com.expediagroup.graphql.types.GraphQLResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Authorization
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer
import no.nav.personbruker.minesaker.api.common.exception.GraphQLResultException
import no.nav.personbruker.minesaker.api.common.exception.SafException
import no.nav.personbruker.minesaker.api.saf.domain.Fodselsnummer
import no.nav.personbruker.minesaker.api.saf.domain.Sakstema
import no.nav.personbruker.minesaker.api.saf.journalposter.JournalposterRequest
import no.nav.personbruker.minesaker.api.saf.sakstemaer.SakstemaerRequest
import no.nav.personbruker.minesaker.api.tokenx.AccessToken
import org.slf4j.LoggerFactory
import java.net.URL

class SafConsumer(
    private val httpClient: HttpClient,
    private val safEndpoint: URL
) {

    private val log = LoggerFactory.getLogger(SafConsumer::class.java)

    suspend fun hentSakstemaer(request: SakstemaerRequest, accessToken: AccessToken): List<Sakstema> {
        val responseDto: GraphQLResponse<HentSakstemaer.Result> = sendQuery(request, accessToken)
        val external = responseDto.extractData()
        logIfContainsDataAndErrors(responseDto)
        return external.toInternal()
    }

    suspend fun hentJournalposter(
        innloggetBruker: Fodselsnummer,
        request: JournalposterRequest,
        accessToken: AccessToken
    ): List<Sakstema> {
        val responseDto: GraphQLResponse<HentJournalposter.Result> = sendQuery(request, accessToken)
        val external = responseDto.extractData()
        logIfContainsDataAndErrors(responseDto)
        return external.toInternal(innloggetBruker)
    }

    private suspend inline fun <reified T> sendQuery(request: GraphQLRequest, accessToken: AccessToken): T =
        runCatching<T> {
            withContext(Dispatchers.IO) {
                httpClient.post {
                    url("$safEndpoint")
                    method = HttpMethod.Post
                    header(Authorization, "Bearer ${accessToken.value}")
                    contentType(ContentType.Application.Json)
                    accept(ContentType.Application.Json)
                    body = request
                }
            }
        }.onFailure { cause ->
            throw SafException("Klarte ikke å utføre spørring mot SAF", cause)
                .addContext("query", request.query)
                .addContext("variables", request.variables)
        }.getOrThrow()

    private inline fun <reified T> GraphQLResponse<T>.extractData(): T {
        return data ?: throw GraphQLResultException("Ingen data i resultatet fra SAF.", errors, extensions)
    }

    private fun logIfContainsDataAndErrors(response: GraphQLResponse<*>) {
        if (response.containsData() && response.containsErrors()) {
            val msg = "Resultatet inneholdt data og feil, dataene returneres til bruker. " +
                    "Feilene var errors: ${response.errors}, extensions: ${response.extensions}"
            log.warn(msg)
        }
    }

    private fun GraphQLResponse<*>.containsData() = data != null
    private fun GraphQLResponse<*>.containsErrors() = errors?.isNotEmpty() == true

}
