package no.nav.tms.minesaker.api.saf

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Authorization
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.utils.io.*
import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer
import no.nav.tms.minesaker.api.exception.CommunicationException
import no.nav.tms.minesaker.api.exception.DocumentNotFoundException
import no.nav.tms.minesaker.api.exception.GraphQLResultException
import no.nav.tms.minesaker.api.config.InnsynsUrlResolver
import no.nav.tms.minesaker.api.domain.JournalposterResponse
import no.nav.tms.minesaker.api.saf.common.GraphQLResponse
import no.nav.tms.minesaker.api.saf.journalposter.JournalposterRequest
import no.nav.tms.minesaker.api.saf.sakstemaer.SakstemaerRequest
import no.nav.tms.minesaker.api.sak.SakstemaResult
import java.net.URL
import java.util.*

class SafConsumer(
    private val httpClient: HttpClient,
    private val safEndpoint: URL,
    private val innsynsUrlResolver: InnsynsUrlResolver
) {

    private val log = KotlinLogging.logger {}
    private val secureLog = KotlinLogging.logger("secureLog")

    private val safCallIdHeaderName = "Nav-Callid"
    private val navConsumerIdHeaderName = "Nav-Consumer-Id"
    private val navConsumerId = "mine-saker-api"

    suspend fun hentSakstemaer(request: SakstemaerRequest, accessToken: String): SakstemaResult {
        return unwrapGraphQLResponse<HentSakstemaer.Result>(sendQuery(request, accessToken))
            .toInternal(innsynsUrlResolver)
    }

    suspend fun hentJournalposter(
        innloggetBruker: String,
        request: JournalposterRequest,
        accessToken: String
    ): JournalposterResponse? {
        val result: HentJournalposter.Result = unwrapGraphQLResponse(sendQuery(request, accessToken))
        return result.toInternal(innloggetBruker)
    }

    suspend fun hentDokument(
        journalpostId: String,
        dokumentinfoId: String,
        accessToken: String,
        receiver: suspend (DokumentStream) -> Unit
    ) {

        val callId = UUID.randomUUID()
        log.info { "Sender POST-kall med correlationId=$callId" }

        val statement = httpClient.prepareGet {
            url("$safEndpoint/rest/hentdokument/$journalpostId/$dokumentinfoId/ARKIV")
            method = HttpMethod.Get
            header(Authorization, "Bearer $accessToken")
            header(safCallIdHeaderName, callId)
            header(navConsumerIdHeaderName, navConsumerId)
        }

        return statement.execute { response ->
            if (response.status == HttpStatusCode.NotFound) {
                throw DocumentNotFoundException(
                    "Fant ikke dokument hos SAF",
                    journalpostId = journalpostId,
                    dokumentinfoId = dokumentinfoId,
                    sensitiveMessage = "Fant ikke dokument hos SAF for url ${response.request.url}"
                )
            } else if (!response.status.isSuccess()) {
                throw CommunicationException("Klarte ikke å hente dokument fra SAF. Http-status [${response.status}]")
            }

            try {
                DokumentStream(
                    response.bodyAsChannel(),
                    response.contentLength()!!,
                    response.contentType() ?: ContentType.Application.Pdf
                ).let {
                    receiver(it)
                }
            } catch (e: Exception) {
                throw CommunicationException("Klarte ikke å lese dokument fra SAF.", e)
            }
        }
    }

    private suspend fun sendQuery(request: GraphQLRequest, accessToken: String): HttpResponse =
        withContext(Dispatchers.IO) {
            val callId = UUID.randomUUID()
            log.info { "Sender graphql-spørring med correlationId=$callId" }
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
        val graphqlResponse = parseBody<T>(response)

        return graphqlResponse.data
            ?: throw GraphQLResultException(
                "Ingen data i resultatet fra SAF.",
                graphqlResponse.errors,
                graphqlResponse.extensions
            )
    }

    private suspend inline fun <reified T> parseBody(response: HttpResponse): GraphQLResponse<T> = try {
        response.body<GraphQLResponse<T>>()
            .also {
                if (it.containsData() && it.containsErrors()) {
                    val baseMsg = "Resultatet inneholdt data og feil, dataene returneres til bruker."
                    log.warn { baseMsg }
                    secureLog.warn {
                        "$baseMsg Feilene var errors: ${it.errors}, extensions: ${it.extensions}"
                    }
                }
            }
    } catch (e: Exception) {
        throw CommunicationException("Klarte ikke tolke respons fra SAF", e)
    }

    private fun GraphQLResponse<*>.containsData() = data != null
    private fun GraphQLResponse<*>.containsErrors() = errors?.isNotEmpty() == true

}

class DokumentStream(
    val channel: ByteReadChannel,
    val size: Long,
    val contentType: ContentType
)
