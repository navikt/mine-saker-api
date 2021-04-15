package no.nav.personbruker.minesaker.api.saf

import com.expediagroup.graphql.types.GraphQLResponse
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer
import no.nav.personbruker.minesaker.api.common.exception.CommunicationException
import no.nav.personbruker.minesaker.api.common.exception.DocumentNotFoundException
import no.nav.personbruker.minesaker.api.common.exception.GraphQLResultException
import no.nav.personbruker.minesaker.api.config.buildJsonSerializer
import no.nav.personbruker.minesaker.api.saf.domain.*
import no.nav.personbruker.minesaker.api.saf.journalposter.JournalposterRequest
import no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers.HentJournalposterResultObjectMother
import no.nav.personbruker.minesaker.api.saf.sakstemaer.objectmothers.HentSakstemaerObjectMother
import no.nav.personbruker.minesaker.api.saf.sakstemaer.SakstemaerRequest
import no.nav.personbruker.minesaker.api.tokenx.AccessToken
import org.amshove.kluent.*
import org.junit.jupiter.api.Test
import java.net.URL

internal class SafConsumerTest {

    private val objectMapper = jacksonObjectMapper()
    private val safDummyEndpoint = URL("https://www.dummy.no")
    private val dummyToken = AccessToken("<access_token>")
    private val dummyIdent = Fodselsnummer("123")

    @Test
    fun `Skal kunne hente sakstemaer`() {
        val externalResponse = HentSakstemaerObjectMother.giveMeOneResult()
        val safResponseAsJson = objectMapper.writeValueAsString(externalResponse)
        val mockHttpClient = createMockHttpClient {
            respond(
                safResponseAsJson,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val consumer = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint)

        val request = SakstemaerRequest.create(dummyIdent)

        val internalSakstema = runBlocking {
            consumer.hentSakstemaer(request, dummyToken)
        }

        val externalSakstema = externalResponse.data!!.dokumentoversiktSelvbetjening.tema
        internalSakstema.size `should be equal to` externalSakstema.size
        internalSakstema[0] `should be instance of` ForenkletSakstema::class
        internalSakstema[0].navn.value `should be equal to` externalSakstema[0].navn
        internalSakstema[0].kode.toString() `should be equal to` externalSakstema[0].kode.toString()
        internalSakstema `should not be equal to` externalSakstema
    }

    @Test
    fun `Skal kunne hente journalposter`() {
        val externalResponse = HentJournalposterResultObjectMother.giveMeOneResult()
        val safResponseAsJson = objectMapper.writeValueAsString(externalResponse)
        val mockHttpClient = createMockHttpClient {
            respond(
                safResponseAsJson,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val consumer = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint)

        val request = JournalposterRequest.create(dummyIdent, Sakstemakode.FOR)

        val internalSakstema = runBlocking {
            consumer.hentJournalposter(dummyIdent, request, dummyToken)
        }

        val externalSakstema = externalResponse.data!!.dokumentoversiktSelvbetjening.tema
        internalSakstema.size `should be equal to` externalSakstema.size
        internalSakstema[0] `should be instance of` Sakstema::class
        internalSakstema[0].navn.value `should be equal to` externalSakstema[0].navn
        internalSakstema[0].kode.toString() `should be equal to` externalSakstema[0].kode.toString()
        internalSakstema `should not be equal to` externalSakstema
    }

    @Test
    fun `Skal takle at det oppstaar en http-feil, og kaste en intern feil videre`() {
        val mockHttpClient = createMockHttpClient {
            respondError(HttpStatusCode.BadRequest)
        }

        val failingConsumer = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint)

        val request = SakstemaerRequest.create(dummyIdent)

        val result = runCatching {
            runBlocking {
                failingConsumer.hentSakstemaer(request, dummyToken)
            }
        }

        result.isFailure `should be equal to` true
        val exception = result.exceptionOrNull()
        exception `should be instance of` CommunicationException::class
    }

    @Test
    fun `Skal takle at graphQL rapporterer en feil, skal da kaste en intern feil videre`() {
        val externalErrorResponse = HentSakstemaerObjectMother.giveMeResponseWithError()
        val safErrorResponseAsJson = objectMapper.writeValueAsString(externalErrorResponse)
        val mockHttpClient = createMockHttpClient {
            respond(
                safErrorResponseAsJson,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val consumer = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint)

        val request = SakstemaerRequest.create(dummyIdent)

        val result = runCatching {
            runBlocking {
                consumer.hentSakstemaer(request, dummyToken)
            }
        }

        result.isFailure `should be equal to` true
        val exception = result.exceptionOrNull()
        exception `should be instance of` GraphQLResultException::class
        exception as GraphQLResultException
        exception.errors?.size `should be equal to` externalErrorResponse.errors?.size
        exception.extensions?.size `should be equal to` externalErrorResponse.extensions?.size
    }

    @Test
    fun `Skal takle uventede feil, og kaste en intern feil videre`() {
        val mockHttpClient = createMockHttpClient {
            respond(
                """{"invalid:"json"}""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val consumerConsistentlyFailing = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint)

        val request = SakstemaerRequest.create(dummyIdent)

        val result = runCatching {
            runBlocking {
                consumerConsistentlyFailing.hentSakstemaer(request, dummyToken)
            }
        }

        result.isFailure `should be equal to` true
        val exception = result.exceptionOrNull()
        exception `should be instance of` CommunicationException::class
        exception as CommunicationException
        exception.context `should have key` "query"
        exception.context `should have key` "variables"
    }

    @Test
    fun `Skal kaste intern feil videre ved tomt data-felt, selv om graphQL ikke har feil i feillisten`() {
        val externalErrorResponse = GraphQLResponse<HentSakstemaer.Result>(data = null)
        val safErrorResponseAsJson = objectMapper.writeValueAsString(externalErrorResponse)
        val mockHttpClient = createMockHttpClient {
            respond(
                safErrorResponseAsJson,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val consumer = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint)

        val request = SakstemaerRequest.create(dummyIdent)

        val result = runCatching {
            runBlocking {
                consumer.hentSakstemaer(request, dummyToken)
            }
        }

        result.isFailure `should be equal to` true
        val exception = result.exceptionOrNull()
        exception `should be instance of` GraphQLResultException::class
    }

    @Test
    fun `Skal returnere data til bruker, hvis det mottas baade data og det rapporteres feil fra SAF`() {
        val externalResponseWithDataAndError = HentSakstemaerObjectMother.giveMeResponseWithDataAndError()
        val safErrorResponseAsJson = objectMapper.writeValueAsString(externalResponseWithDataAndError)
        val mockHttpClient = createMockHttpClient {
            respond(
                safErrorResponseAsJson,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val consumer = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint)

        val request = SakstemaerRequest.create(dummyIdent)

        val result = runCatching {
            runBlocking {
                consumer.hentSakstemaer(request, dummyToken)
            }
        }

        result.isSuccess `should be equal to` true
        val dto = result.getOrThrow()
        dto.size `should be equal to` externalResponseWithDataAndError.data?.dokumentoversiktSelvbetjening?.tema?.size
    }

    @Test
    fun `Skal kunne hente et dokument`() {
        val dummyBinaryDataResponse = "dummy data for å simulere binære data".toByteArray()
        val mockHttpClient = createMockHttpClient {
            respond(dummyBinaryDataResponse)
        }
        val consumer = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint)

        val dokumentAsByteArray = runBlocking {
            consumer.hentDokument(JournalpostId("123"), DokumentInfoId("456"), dummyToken)
        }

        dokumentAsByteArray.size `should be equal to` dummyBinaryDataResponse.size
        dokumentAsByteArray `should be equal to` dummyBinaryDataResponse
    }

    @Test
    fun `Skal takle at et dokument ikke blir funnet`() {
        val expectedErrorCode = HttpStatusCode.NotFound
        val mockHttpClient = createMockHttpClient {
            respondError(status = expectedErrorCode)
        }
        val consumer = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint)

        val result = runCatching {
            runBlocking {
                consumer.hentDokument(JournalpostId("123"), DokumentInfoId("456"), dummyToken)
            }
        }

        result.isFailure `should be equal to` true
        val exception = result.exceptionOrNull()
        exception as DocumentNotFoundException
        exception `should be instance of` DocumentNotFoundException::class
        exception.context `should have key` "journalpostId"
        exception.context `should have key` "dokumentinfoId"

        val cause = exception.cause
        cause as ClientRequestException
        cause `should be instance of` ClientRequestException::class
        exception.cause?.message?.`should contain`(expectedErrorCode.value.toString())
    }

    @Test
    fun `Skal takle alle andre feil som ikke er NotFound for dokument`() {
        val expectedErrorCode = HttpStatusCode.InternalServerError
        val mockHttpClient = createMockHttpClient {
            respondError(status = expectedErrorCode)
        }
        val consumer = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint)

        val result = runCatching {
            runBlocking {
                consumer.hentDokument(JournalpostId("123"), DokumentInfoId("456"), dummyToken)
            }
        }

        result.isFailure `should be equal to` true
        val exception = result.exceptionOrNull()
        exception as CommunicationException
        exception.context `should have key` "journalpostId"
        exception.context `should have key` "dokumentinfoId"
        exception.cause?.message?.`should contain`(expectedErrorCode.value.toString())
    }

    private fun createMockHttpClient(respond: MockRequestHandleScope.() -> HttpResponseData): HttpClient {
        return HttpClient(MockEngine) {
            engine {
                addHandler {
                    respond()
                }
            }
            install(JsonFeature) {
                serializer = buildJsonSerializer()
            }
        }
    }

}
