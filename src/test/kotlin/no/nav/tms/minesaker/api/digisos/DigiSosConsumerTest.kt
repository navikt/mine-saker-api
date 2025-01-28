package no.nav.tms.minesaker.api.digisos

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import kotlinx.coroutines.runBlocking
import no.nav.tms.minesaker.api.setup.jsonConfig

import no.nav.tms.minesaker.api.setup.CommunicationException
import no.nav.tms.minesaker.api.setup.createUrl
import org.junit.jupiter.api.Test

import java.time.LocalDateTime

internal class DigiSosConsumerTest {

    private val objectMapper = jacksonObjectMapper().apply {
        registerKotlinModule()
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }
    private val digiSosDummyEndpoint = createUrl("https://www.dummy.no")
    private val dummyToken = "<access_token>"

    @Test
    fun `returnerer true hvis bruker har noen innsendte søknader`() {
        val externalResponse = listOf(responseSisteEndretEnUkeSiden())
        val responseAsJson = objectMapper.writeValueAsString(externalResponse)
        val mockHttpClient = createMockHttpClient {
            respond(
                responseAsJson,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val consumer = DigiSosConsumer(mockHttpClient, digiSosEndpoint = digiSosDummyEndpoint)

        val harInnsendte = runBlocking {
            consumer.harInnsendte(dummyToken)
        }

        harInnsendte shouldBe true
    }

    @Test
    fun `returnerer true hvis bruker ikke har innsendte søknader`() {
        val mockHttpClient = createMockHttpClient {
            respond(
                "[]",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val consumer = DigiSosConsumer(mockHttpClient, digiSosEndpoint = digiSosDummyEndpoint)

        val harInnsendte = runBlocking {
            consumer.harInnsendte(dummyToken)
        }

        harInnsendte shouldBe false
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
        val consumer = DigiSosConsumer(mockHttpClient, digiSosEndpoint = digiSosDummyEndpoint)

        shouldThrow<CommunicationException> {
            runBlocking {
                consumer.harInnsendte(dummyToken)
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
