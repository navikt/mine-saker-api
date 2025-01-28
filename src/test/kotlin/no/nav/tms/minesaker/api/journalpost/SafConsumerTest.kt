package no.nav.tms.minesaker.api.journalpost

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import no.nav.dokument.saf.selvbetjening.generated.dto.AlleJournalposter
import no.nav.tms.minesaker.api.journalpost.query.AlleJournalposterRequest
import no.nav.tms.minesaker.api.journalpost.query.GraphQLError
import no.nav.tms.minesaker.api.journalpost.query.GraphQLResponse
import no.nav.tms.minesaker.api.setup.*

import org.junit.jupiter.api.Test

internal class SafConsumerTest {

    private val objectMapper = jacksonObjectMapper()
    private val safDummyEndpoint = createUrl("https://www.dummy.no")
    private val dummyToken = "<access_token>"
    private val dummyIdent = "123"

    @Test
    fun `Skal kunne hente journalposter`() {
        val externalResponse = GraphQLResponse(AlleJournalposterResultTestData.journalposterResult())
        val safResponseAsJson = objectMapper.writeValueAsString(externalResponse)
        val mockHttpClient = createMockHttpClient {
            respond(
                safResponseAsJson,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val consumer = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint)

        val request = AlleJournalposterRequest.create(dummyIdent)

        val internalSakstema = runBlocking {
            consumer.alleJournalposter(request, dummyToken)
        }

        val externalSakstema = externalResponse.data!!.dokumentoversiktSelvbetjening.journalposter
        internalSakstema.shouldBeInstanceOf<List<Journalpost>>()
        internalSakstema.first().temakode shouldBe externalSakstema[0].tema
        internalSakstema shouldNotBe externalSakstema
    }

    @Test
    fun `Skal takle at det oppstaar en http-feil, og kaste en intern feil videre`() {
        val mockHttpClient = createMockHttpClient {
            respondError(HttpStatusCode.BadRequest)
        }

        val failingConsumer = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint)

        val request = AlleJournalposterRequest.create(dummyIdent)

        val result = runCatching {
            runBlocking {
                failingConsumer.alleJournalposter(request, dummyToken)
            }
        }

        result.isFailure shouldBe true
        val exception = result.exceptionOrNull()
        exception.shouldBeInstanceOf<CommunicationException>()
    }

    @Test
    fun `Skal takle at graphQL rapporterer en feil, skal da kaste en intern feil videre`() {
        val externalErrorResponse = responseWithError()
        val safErrorResponseAsJson = objectMapper.writeValueAsString(externalErrorResponse)
        val mockHttpClient = createMockHttpClient {
            respond(
                safErrorResponseAsJson,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val consumer = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint)

        val request = AlleJournalposterRequest.create(dummyIdent)

        val result = runCatching {
            runBlocking {
                consumer.alleJournalposter(request, dummyToken)
            }
        }

        result.isFailure shouldBe true
        val exception = result.exceptionOrNull()
        exception.shouldBeInstanceOf<SafResultException>()
        exception.errors?.size shouldBe externalErrorResponse.errors?.size
        exception.extensions?.size shouldBe externalErrorResponse.extensions?.size
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

        val request = AlleJournalposterRequest.create(dummyIdent)

        val result = kotlin.runCatching {
            runBlocking {
                consumerConsistentlyFailing.alleJournalposter(request, dummyToken)
            }
        }

        result.isFailure shouldBe true
        val exception = result.exceptionOrNull()
        exception.shouldBeInstanceOf<CommunicationException>()
    }

    @Test
    fun `Skal kaste intern feil videre ved tomt data-felt, selv om graphQL ikke har feil i feillisten`() {
        val externalErrorResponse = GraphQLResponse<Unit>()
        val safErrorResponseAsJson = objectMapper.writeValueAsString(externalErrorResponse)
        val mockHttpClient = createMockHttpClient {
            respond(
                safErrorResponseAsJson,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val consumer = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint)

        val request = AlleJournalposterRequest.create(dummyIdent)

        val result = runCatching {
            runBlocking {
                consumer.alleJournalposter(request, dummyToken)
            }
        }

        result.isFailure shouldBe true
        val exception = result.exceptionOrNull()
        exception.shouldBeInstanceOf<SafResultException>()
    }

    @Test
    fun `Skal kunne hente et dokument`() {
        val dummyBinaryDataResponse = "dummy data for å simulere binære data".toByteArray()
        val mockHttpClient = createMockHttpClient {
            respond(
                dummyBinaryDataResponse,
                headers = headers {
                    append(HttpHeaders.ContentLength, dummyBinaryDataResponse.size.toString())
                }
            )
        }
        val consumer = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint)

        runBlocking {
            consumer.hentDokument("123", "456", dummyToken) {
                it.size shouldBe dummyBinaryDataResponse.size

                it.receiveBody() shouldBe dummyBinaryDataResponse
            }
        }
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
                consumer.hentDokument("123", "456", dummyToken) {}
            }
        }

        result.isFailure shouldBe true
        val exception = result.exceptionOrNull()
        exception.shouldBeInstanceOf<DocumentNotFoundException>()
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
                consumer.hentDokument("123", "456", dummyToken) {}
            }
        }

        result.isFailure shouldBe true
        val exception = result.exceptionOrNull()
        exception as CommunicationException
    }

    @Test
    fun `Skal takle at dokument fra saf er av annen type enn pdf`() {
        val dummyBinaryDataResponse = """{"dokument": "response"}""".toByteArray()
        val mockHttpClient = createMockHttpClient {
            respond(
                dummyBinaryDataResponse,
                headers = headers {
                    append(HttpHeaders.ContentLength, dummyBinaryDataResponse.size.toString())
                    append(HttpHeaders.ContentType, ContentType.Application.Json)
                }
            )
        }
        val consumer = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint)

        runBlocking {
            consumer.hentDokument("123", "456", dummyToken) {
                it.size shouldBe dummyBinaryDataResponse.size
                it.contentType shouldBe ContentType.Application.Json
                it.receiveBody() shouldBe dummyBinaryDataResponse
            }
        }
    }

    private fun createMockHttpClient(respond: MockRequestHandleScope.() -> HttpResponseData): HttpClient {
        return HttpClient(MockEngine) {
            engine {
                addHandler {
                    respond()
                }
            }
            install(ContentNegotiation) {
                jackson {
                    jsonConfig()
                }
            }
            install(HttpTimeout)
        }
    }
}

private suspend fun DokumentStream.receiveBody(): ByteArray {
    val buffer = ByteArray(size.toInt())

    channel.readAvailable(buffer)

    return buffer
}

private fun response(): GraphQLResponse<AlleJournalposter.Result> {
    val data = AlleJournalposterResultTestData.journalposterResult()
    return GraphQLResponse(data)
}

private fun responseWithError(data: AlleJournalposter.Result? = null): GraphQLResponse<AlleJournalposter.Result> {
    val error = GraphQLError("Feilet ved henting av data for bruker.")

    return GraphQLResponse(data, listOf(error))
}
