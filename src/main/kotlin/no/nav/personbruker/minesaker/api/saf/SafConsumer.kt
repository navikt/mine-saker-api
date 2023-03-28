package no.nav.personbruker.minesaker.api.saf

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Authorization
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer
import no.nav.personbruker.minesaker.api.common.exception.CommunicationException
import no.nav.personbruker.minesaker.api.common.exception.DocumentNotFoundException
import no.nav.personbruker.minesaker.api.common.exception.GraphQLResultException
import no.nav.personbruker.minesaker.api.domain.Sakstema
import no.nav.personbruker.minesaker.api.saf.common.GraphQLResponse
import no.nav.personbruker.minesaker.api.saf.journalposter.JournalposterRequest
import no.nav.personbruker.minesaker.api.saf.sakstemaer.SakstemaerRequest
import no.nav.personbruker.minesaker.api.sak.Kildetype
import no.nav.personbruker.minesaker.api.sak.SakstemaResult
import java.net.URL
import java.util.*

class SafConsumer(
    private val httpClient: HttpClient,
    private val safEndpoint: URL
) {

    private val log = KotlinLogging.logger {}

    private val safCallIdHeaderName = "Nav-Callid"
    private val navConsumerIdHeaderName = "Nav-Consumer-Id"

    private val navConsumerId = "mine-saker-api"

    suspend fun hentSakstemaer(request: SakstemaerRequest, accessToken: String): SakstemaResult {
        return try {
            val result: HentSakstemaer.Result = unwrapGraphQLResponse(sendQuery(request, accessToken))

            SakstemaResult(result.toInternal())

        } catch (e: Exception) {
            log.warn("Klarte ikke å hente data fra SAF.", e)
            SakstemaResult(errors = listOf(Kildetype.SAF))
        }
    }

    suspend fun hentJournalposter(
        innloggetBruker: String,
        request: JournalposterRequest,
        accessToken: String
    ): List<Sakstema> {
        val result: HentJournalposter.Result = unwrapGraphQLResponse(sendQuery(request, accessToken))
        return result.toInternal(innloggetBruker)
    }

    suspend fun hentDokument(
        journapostId: String,
        dokumentinfoId: String,
        accessToken: String
    ): ByteArray {
        try {
            val httpResponse = fetchDocument(journapostId, dokumentinfoId, accessToken)
            return unpackRawResponseBody(httpResponse)
        } catch (e: Exception) {
            throw e;
        }

    }

    private suspend fun fetchDocument(
        journapostId: String,
        dokumentinfoId: String,
        accessToken: String
    ): HttpResponse = withContext(Dispatchers.IO) {
        val callId = UUID.randomUUID()
        log.info("Sender POST-kall med correlationId=$callId")
        val urlToFetch = "$safEndpoint/rest/hentdokument/$journapostId/$dokumentinfoId/ARKIV"
        log.info("Skal hente data fra: $urlToFetch")
        httpClient.request {
            url(urlToFetch)
            method = HttpMethod.Get
            header(Authorization, "Bearer $accessToken")
            header(safCallIdHeaderName, callId)
            header(navConsumerIdHeaderName, navConsumerId)
        }
    }

    private suspend fun unpackRawResponseBody(response: HttpResponse): ByteArray {
        if (response.status == HttpStatusCode.NotFound) {
            throw DocumentNotFoundException(
                "Fant ikke dokument hos SAF",
                sensitiveMessage = "Fant ikke dokument hos SAF for url ${response.request.url}"
            )
        } else if (!response.status.isSuccess()) {
            throw CommunicationException("Klarte ikke å hente dokument fra SAF. Http-status [${response.status}]")
        }

        try {
            return response.readBytes()
        } catch (e: Exception) {
            throw CommunicationException("Klarte ikke å lese dokument fra SAF.", e)
        }
    }

    private suspend fun sendQuery(request: GraphQLRequest, accessToken: String): HttpResponse =
        withContext(Dispatchers.IO) {
            val callId = UUID.randomUUID()
            log.info("Sender graphql-spørring med correlationId=$callId")
            httpClient.post {
                url("$safEndpoint/graphql")
                method = HttpMethod.Post
                header(safCallIdHeaderName, callId)
                header(Authorization, "Bearer $accessToken")
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(request)
                timeout {
                    socketTimeoutMillis = 25000
                    connectTimeoutMillis = 10000
                    requestTimeoutMillis = 35000
                }
            }
        }

    private suspend inline fun <reified T> unwrapGraphQLResponse(response: HttpResponse): T {
        if (!response.status.isSuccess()) {
            throw CommunicationException("Fikk http-status [${response.status}] fra SAF.")
        }

        val graphQLResponse = parseBody<T>(response)

        logIfContainsDataAndErrors(graphQLResponse)

        return graphQLResponse.extractData()
    }

    private suspend inline fun <reified T> parseBody(response: HttpResponse): GraphQLResponse<T> {
        return try {
            response.body()
        } catch (e: Exception) {
            throw CommunicationException("Klarte ikke tolke respons fra SAD", e)
        }
    }

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
