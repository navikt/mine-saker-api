package no.nav.personbruker.minesaker.api.saf

import com.expediagroup.graphql.types.GraphQLResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
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

    suspend fun hentSakstemaerTriggFeil(request: SakstemaerRequest, accessToken: AccessToken): List<Sakstema> {
        val responseDto: String = runCatching {
            val response: HttpResponse = sendQuery(request, accessToken)
            val graphQLResponseDto = response.receive<String>()
            graphQLResponseDto

        }.onFailure { cause ->
            throw SafException("Klarte ikke å utføre spørring mot SAF", cause)
                .addContext("query", request.query)
                .addContext("variables", request.variables)

        }.getOrThrow()
        log.info("Mottatt response:\n$responseDto")

        return emptyList()
    }

    suspend fun hentSakstemaer(request: SakstemaerRequest, accessToken: AccessToken): List<Sakstema> {
        val responseDto: GraphQLResponse<HentSakstemaer.Result> = fetchResultAndHandleErrors(request, accessToken)
        val external: HentSakstemaer.Result = responseDto.data ?: throw noDataWithContext(responseDto)
        return external.toInternal()
    }

    suspend fun hentJournalposter(
        innloggetBruker: Fodselsnummer,
        request: JournalposterRequest,
        accessToken: AccessToken
    ): List<Sakstema> {
        val responseDto: GraphQLResponse<HentJournalposter.Result> = fetchResultAndHandleErrors(request, accessToken)
        val external: HentJournalposter.Result = responseDto.data ?: throw noDataWithContext(responseDto)
        return external.toInternal(innloggetBruker)
    }

    private suspend inline fun <reified T : GraphQLResponse<*>> fetchResultAndHandleErrors(
        request: GraphQLRequest,
        accessToken: AccessToken
    ): T = runCatching {
        val response: HttpResponse = sendQuery(request, accessToken)
        val graphQLResponseDto = response.receive<T>()
        graphQLResponseDto.verifyThatResultDoesNotHaveErrors()
        graphQLResponseDto

    }.onFailure { cause ->
        throw SafException("Klarte ikke å utføre spørring mot SAF", cause)
            .addContext("query", request.query)
            .addContext("variables", request.variables)

    }.getOrThrow()

    private suspend inline fun <reified T> sendQuery(request: GraphQLRequest, accessToken: AccessToken): T =
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

    private fun GraphQLResponse<*>.verifyThatResultDoesNotHaveErrors() {
        if (errors?.isNotEmpty() == true) {
            val message = "Det skjedde en feil i spørringen mot SAF. Se feillisten for detaljer."
            throw GraphQLResultException(message, errors, extensions)
        }
    }

    private fun noDataWithContext(responseDto: GraphQLResponse<*>) =
        SafException("Ingen data i resultatet fra SAF.")
            .addContext("errors", responseDto.errors)
            .addContext("extensions", responseDto.extensions)

}
