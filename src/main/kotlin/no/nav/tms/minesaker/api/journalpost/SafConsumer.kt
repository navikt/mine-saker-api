package no.nav.tms.minesaker.api.journalpost

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
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import no.nav.dokument.saf.selvbetjening.generated.dto.*
import no.nav.tms.minesaker.api.journalpost.query.*
import no.nav.tms.minesaker.api.setup.*
import java.net.URL
import java.util.*

class SafConsumer(
    private val httpClient: HttpClient,
    private val safEndpoint: URL
) {

    private val log = KotlinLogging.logger {}
    private val secureLog = KotlinLogging.logger("secureLog")

    private val safCallIdHeaderName = "Nav-Callid"
    private val navConsumerIdHeaderName = "Nav-Consumer-Id"
    private val navConsumerId = "mine-saker-api"

    suspend fun alleJournalposter(
        request: AlleJournalposterRequest,
        accessToken: String
    ): List<Journalpost> {
        val result: AlleJournalposter.Result = unwrapSafResponse(sendQuery(request, accessToken))
        return result.toInternal()
    }

    suspend fun hentJournalpost(
        request: HentJournalpostV2Request,
        accessToken: String
    ): Journalpost? {
        val result: HentJournalpost.Result = unwrapSafResponse(sendQuery(request, accessToken))
        return result.toInternal()
    }

    suspend fun hentDokument(
        journalpostId: String,
        dokumentinfoId: String,
        erSladdet: Boolean,
        accessToken: String,
        receiver: suspend (DokumentStream) -> Unit
    ) {

        val callId = UUID.randomUUID()
        log.info { "Sender POST-kall med correlationId=$callId" }

        val visningsFormat = if (erSladdet) "SLADDET" else "ARKIV"

        val statement = httpClient.prepareGet {
            url("$safEndpoint/rest/hentdokument/$journalpostId/$dokumentinfoId/$visningsFormat")
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
            } else if (response.status == HttpStatusCode.Forbidden) {
                val requestedVariant = if (erSladdet) "SLADDET" else "ARKIV"

                throw DocumentFormatNotAvailableException(
                    "Dokumentet hos SAF kan ikke vises med variant [$requestedVariant]",
                    journalpostId = journalpostId,
                    dokumentinfoId = dokumentinfoId,
                    requestedVariant = requestedVariant
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
            } catch (e: ChannelWriteException) {

                throw PrematureClientCloseException(
                    cause = e,
                    journalpostId = journalpostId,
                    dokumentId = dokumentinfoId,
                    fileType = response.contentType().toString(),
                    fileSize = response.contentLength() ?: 0
                )
            } catch (e: Exception) {
                secureLog.error(e) { "Feil ved streaming av dokument fra SAF [journalpostId: $journalpostId, type: ${response.contentType()}, størrelse: ${response.contentLength()}]" }
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

    private suspend inline fun <reified T> unwrapSafResponse(response: HttpResponse): T {
        if (!response.status.isSuccess()) {
            throw CommunicationException("Fikk http-status [${response.status}] fra SAF.")
        }
        val SafResponse = parseBody<T>(response)

        return SafResponse.data
            ?: throw SafResultException(
                "Ingen data i resultatet fra SAF.",
                SafResponse.errors,
                SafResponse.extensions
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
