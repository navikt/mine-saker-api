package no.nav.personbruker.minesaker.api.saf

import com.expediagroup.graphql.types.GraphQLResponse
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Authorization
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer
import no.nav.personbruker.minesaker.api.common.exception.AbstractMineSakerException
import no.nav.personbruker.minesaker.api.common.exception.CommunicationException
import no.nav.personbruker.minesaker.api.common.exception.DocumentNotFoundException
import no.nav.personbruker.minesaker.api.common.exception.GraphQLResultException
import no.nav.personbruker.minesaker.api.domain.*
import no.nav.personbruker.minesaker.api.saf.journalposter.JournalposterRequest
import no.nav.personbruker.minesaker.api.saf.sakstemaer.SakstemaerRequest
import no.nav.personbruker.minesaker.api.tokenx.AccessToken
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*

class SafConsumer(
    private val httpClient: HttpClient,
    private val safEndpoint: URL
) {

    private val log = LoggerFactory.getLogger(SafConsumer::class.java)

    private val NavCallIdHeaderName = "Nav-Call-Id"

    suspend fun hentSakstemaer(request: SakstemaerRequest, accessToken: AccessToken): List<ForenkletSakstema> {
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

    suspend fun hentDokument(
        journapostId: JournalpostId,
        dokumentinfoId: DokumentInfoId,
        accessToken: AccessToken
    ): ByteArray {
        val response = fetchDocument(journapostId, dokumentinfoId, accessToken)
        return extractBinaryData(response, journapostId, dokumentinfoId)
    }

    private suspend fun fetchDocument(
        journapostId: JournalpostId,
        dokumentinfoId: DokumentInfoId,
        accessToken: AccessToken
    ): HttpResponse = runCatching {
        withContext<HttpResponse>(Dispatchers.IO) {
            val callId = UUID.randomUUID()
            log.info("Sender POST-kall med correlationId=$callId")
            val urlToFetch = "$safEndpoint/rest/hentdokument/$journapostId/$dokumentinfoId/ARKIV"
            log.info("Skal hente data fra: $urlToFetch")
            httpClient.request {
                url(urlToFetch)
                method = HttpMethod.Get
                header(Authorization, "Bearer ${accessToken.value}")
                header(NavCallIdHeaderName, callId)
            }
        }
    }.onFailure { cause ->
        throw handleDocumentExceptionAndBuildInternalException(cause, journapostId, dokumentinfoId)
    }.getOrThrow()

    private suspend fun extractBinaryData(
        response: HttpResponse,
        journapostId: JournalpostId,
        dokumentinfoId: DokumentInfoId
    ): ByteArray = runCatching {
        response.readBytes()

    }.onFailure { cause ->
        throw CommunicationException("Klarte ikke å lese inn dataene i responsen fra SAF", cause)
            .addContext("journapostId", journapostId)
            .addContext("dokumentinfoId", dokumentinfoId)
    }.getOrThrow()

    private fun handleDocumentExceptionAndBuildInternalException(
        exception: Throwable,
        journapostId: JournalpostId,
        dokumentinfoId: DokumentInfoId
    ): AbstractMineSakerException {
        val internalException = if (exception is ClientRequestException && exception.isResponseCodeIsNotFound()) {
            DocumentNotFoundException("Dokumentet ble ikke funnet.", exception)
        } else {
            CommunicationException("Klarte ikke å hente dokumentet", exception)
        }

        return internalException
            .addContext("journalpostId", journapostId)
            .addContext("dokumentinfoId", dokumentinfoId)
    }

    private fun ClientRequestException.isResponseCodeIsNotFound(): Boolean = response.status == HttpStatusCode.NotFound

    private suspend inline fun <reified T> sendQuery(request: GraphQLRequest, accessToken: AccessToken): T =
        runCatching<T> {
            val callId = UUID.randomUUID()
            log.info("Sender graphql-spørring med correlationId=$callId")
            withContext(Dispatchers.IO) {
                httpClient.post {
                    url("$safEndpoint/graphql")
                    method = HttpMethod.Post
                    header(NavCallIdHeaderName, callId)
                    header(Authorization, "Bearer ${accessToken.value}")
                    contentType(ContentType.Application.Json)
                    accept(ContentType.Application.Json)
                    body = request
                }
            }
        }.onFailure { cause ->
            throw CommunicationException("Klarte ikke å utføre spørring mot SAF", cause)
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
