package no.nav.tms.minesaker.api.saf

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.assertions.throwables.shouldThrow
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
import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer
import no.nav.tms.minesaker.api.exception.CommunicationException
import no.nav.tms.minesaker.api.exception.DocumentNotFoundException
import no.nav.tms.minesaker.api.exception.GraphQLResultException
import no.nav.tms.minesaker.api.config.InnsynsUrlResolver
import no.nav.tms.minesaker.api.config.jsonConfig
import no.nav.tms.minesaker.api.domain.*
import no.nav.tms.minesaker.api.saf.common.GraphQLError
import no.nav.tms.minesaker.api.saf.common.GraphQLResponse
import no.nav.tms.minesaker.api.saf.journalposter.JournalposterRequest
import no.nav.tms.minesaker.api.saf.journalposter.HentJournalposterResultTestData
import no.nav.tms.minesaker.api.saf.sakstemaer.HentSakstemaResultTestData
import no.nav.tms.minesaker.api.saf.sakstemaer.SakstemaerRequest

import org.junit.jupiter.api.Test
import java.net.URL

internal class SafConsumerTest {

    private val objectMapper = jacksonObjectMapper()
    private val safDummyEndpoint = URL("https://www.dummy.no")
    private val dummyToken = "<access_token>"
    private val dummyIdent = "123"
    private val dummyUrlResolver = InnsynsUrlResolver(mapOf(), "http://dummy.innsyn.no")

    @Test
    fun `Skal kunne hente sakstemaer`() {
        val externalResponse = response()
        val safResponseAsJson = objectMapper.writeValueAsString(externalResponse)
        val mockHttpClient = createMockHttpClient {
            respond(
                safResponseAsJson,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val consumer = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint, dummyUrlResolver)

        val request = SakstemaerRequest.create(dummyIdent)

        val sakstemarespons = runBlocking {
            consumer.hentSakstemaer(request, dummyToken)
        }

        val externalSakstema = externalResponse.data!!.dokumentoversiktSelvbetjening.tema
        sakstemarespons.resultsSorted().size shouldBe externalSakstema.size
        sakstemarespons.resultsSorted()[0].shouldBeInstanceOf<ForenkletSakstema>()
        sakstemarespons.resultsSorted()[0].navn shouldBe externalSakstema[0].navn
        sakstemarespons.resultsSorted()[0].kode.toString() shouldBe externalSakstema[0].kode
        sakstemarespons shouldNotBe externalSakstema
    }

    @Test
    fun `Hvis henting av sakstema returnerer generisk feil, skal det kastes CommunicationException`() {
        val invalidJsonResponseSomVilTriggeEnException = "invalid response"
        val mockHttpClient = createMockHttpClient {
            respond(
                invalidJsonResponseSomVilTriggeEnException,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val consumer = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint, dummyUrlResolver)

        val request = SakstemaerRequest.create(dummyIdent)

        shouldThrow<CommunicationException> {
            runBlocking {
                consumer.hentSakstemaer(request, dummyToken)
            }
        }
    }

    @Test
    fun `Hvis henting av sakstema returnerer resultat uten data, skal det kastes GraphQLException`() {
        val feilrespons =  """{ "data": null }"""
        val mockHttpClient = createMockHttpClient {
            respond(
                feilrespons,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val consumer = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint, dummyUrlResolver)

        val request = SakstemaerRequest.create(dummyIdent)

        shouldThrow<GraphQLResultException> {
            runBlocking {
                consumer.hentSakstemaer(request, dummyToken)
            }
        }
    }

    @Test
    fun `Skal kunne hente journalposter`() {
        val externalResponse = GraphQLResponse(HentJournalposterResultTestData.journalposterResult())
        val safResponseAsJson = objectMapper.writeValueAsString(externalResponse)
        val mockHttpClient = createMockHttpClient {
            respond(
                safResponseAsJson,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val consumer = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint, dummyUrlResolver)

        val request = JournalposterRequest.create(dummyIdent, Sakstemakode.FOR)

        val internalSakstema = runBlocking {
            consumer.hentJournalposter(dummyIdent, request, dummyToken)
        }

        val externalSakstema = externalResponse.data!!.dokumentoversiktSelvbetjening.tema
        internalSakstema.shouldBeInstanceOf<JournalposterResponse>()
        internalSakstema.navn shouldBe externalSakstema[0].navn
        internalSakstema.kode.toString() shouldBe externalSakstema[0].kode
        internalSakstema shouldNotBe  externalSakstema
    }

    @Test
    fun `Skal takle at det oppstaar en http-feil, og kaste en intern feil videre`() {
        val mockHttpClient = createMockHttpClient {
            respondError(HttpStatusCode.BadRequest)
        }

        val failingConsumer = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint, dummyUrlResolver)

        val request = JournalposterRequest.create(dummyIdent, Sakstemakode.FOR)

        val result = runCatching {
            runBlocking {
                failingConsumer.hentJournalposter(dummyIdent, request, dummyToken)
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
        val consumer = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint, dummyUrlResolver)

        val request = JournalposterRequest.create(dummyIdent, Sakstemakode.FOR)

        val result = runCatching {
            runBlocking {
                consumer.hentJournalposter(dummyIdent, request, dummyToken)
            }
        }

        result.isFailure shouldBe true
        val exception = result.exceptionOrNull()
        exception.shouldBeInstanceOf<GraphQLResultException>()
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
        val consumerConsistentlyFailing = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint, dummyUrlResolver)

        val request = JournalposterRequest.create(dummyIdent, Sakstemakode.DAG)

        val result = kotlin.runCatching {
            runBlocking {
                consumerConsistentlyFailing.hentJournalposter(dummyIdent, request, dummyToken)
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
        val consumer = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint, dummyUrlResolver)

        val request = JournalposterRequest.create(dummyIdent, Sakstemakode.FOR)

        val result = runCatching {
            runBlocking {
                consumer.hentJournalposter(dummyIdent, request, dummyToken)
            }
        }

        result.isFailure shouldBe true
        val exception = result.exceptionOrNull()
        exception.shouldBeInstanceOf<GraphQLResultException>()
    }

    @Test
    fun `Skal returnere data til bruker, hvis det mottas baade data og det rapporteres feil fra SAF`() {
        val externalResponseWithDataAndError = HentJournalposterResultTestData.responseWithDataAndError()
        val safErrorResponseAsJson = objectMapper.writeValueAsString(externalResponseWithDataAndError)
        val mockHttpClient = createMockHttpClient {
            respond(
                safErrorResponseAsJson,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val consumer = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint, dummyUrlResolver)

        val request = JournalposterRequest.create(dummyIdent, Sakstemakode.FOR)

        val result = runCatching {
            runBlocking {
                consumer.hentJournalposter(dummyIdent, request, dummyToken)
            }
        }

        result.isSuccess shouldBe true
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
        val consumer = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint, dummyUrlResolver)

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
        val consumer = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint, dummyUrlResolver)

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
        val consumer = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint, dummyUrlResolver)

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
        val consumer = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint, dummyUrlResolver)

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

private fun response(): GraphQLResponse<HentSakstemaer.Result> {
    val data = HentSakstemaResultTestData.result()
    return GraphQLResponse(data)
}

private fun responseWithError(data: HentSakstemaer.Result? = null): GraphQLResponse<HentSakstemaer.Result> {
    val error = GraphQLError("Feilet ved henting av data for bruker.")

    return GraphQLResponse(data, listOf(error))
}
