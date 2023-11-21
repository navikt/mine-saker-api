package no.nav.tms.minesaker.api.digisos

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import kotlinx.coroutines.runBlocking
import no.nav.tms.minesaker.api.config.InnsynsUrlResolver
import no.nav.tms.minesaker.api.config.jsonConfig

import no.nav.tms.minesaker.api.domain.ForenkletSakstema
import no.nav.tms.minesaker.api.exception.CommunicationException
import org.junit.jupiter.api.Test

import java.net.URL
import java.time.LocalDateTime

internal class DigiSosConsumerTest {

    private val objectMapper = jacksonObjectMapper().apply {
        registerKotlinModule()
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }
    private val digiSosDummyEndpoint = URL("https://www.dummy.no")
    private val dummyToken = "<access_token>"
    private val dummyResolver = InnsynsUrlResolver(mapOf(), "http://dummy.innsyn.no")

    @Test
    fun `Skal kunne hente sakstemaer`() {
        val externalResponse = listOf(responseSisteEndretEnUkeSiden())
        val responseAsJson = objectMapper.writeValueAsString(externalResponse)
        val mockHttpClient = createMockHttpClient {
            respond(
                responseAsJson,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val consumer = DigiSosConsumer(mockHttpClient, digiSosEndpoint = digiSosDummyEndpoint,dummyResolver)

        val internalSakstema = runBlocking {
            consumer.hentSakstemaer(dummyToken)
        }

        internalSakstema.resultsSorted().size shouldBe externalResponse.size
        internalSakstema.resultsSorted()[0].shouldBeInstanceOf<ForenkletSakstema>()
        internalSakstema.resultsSorted()[0].navn shouldBe externalResponse[0].navn
        internalSakstema.resultsSorted()[0].kode.toString() shouldBe externalResponse[0].kode
        internalSakstema shouldNotBe externalResponse
    }

    @Test
    fun `Hvis henting av sakstema feiler, saa skal det kastes exception`() {
        val invalidJsonResponseSomVilTriggeEnException = "invalid response"
        val mockHttpClient = createMockHttpClient {
            respond(
                invalidJsonResponseSomVilTriggeEnException,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val consumer = DigiSosConsumer(mockHttpClient, digiSosEndpoint = digiSosDummyEndpoint, dummyResolver)

        shouldThrow<CommunicationException> {
            runBlocking {
                consumer.hentSakstemaer(dummyToken)
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
        }
    }

}

private fun responseSisteEndretEnUkeSiden() = DigiSosResponse(
    "Økonomisk sosialhjelp",
    "KOM",
    LocalDateTime.now().minusWeeks(1)
)
