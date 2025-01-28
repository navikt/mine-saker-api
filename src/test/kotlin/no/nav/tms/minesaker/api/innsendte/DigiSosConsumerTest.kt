package no.nav.tms.minesaker.api.innsendte

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
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.tms.minesaker.api.setup.jsonConfig

import no.nav.tms.minesaker.api.setup.CommunicationException
import no.nav.tms.minesaker.api.setup.TokendingsExchange
import no.nav.tms.minesaker.api.setup.createUrl
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser
import org.junit.jupiter.api.Test

import java.time.LocalDateTime

internal class DigiSosConsumerTest {

    private val objectMapper = jacksonObjectMapper().apply {
        registerKotlinModule()
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }
    private val digiSosEndpoint = createUrl("https://www.dummy.no")
    private val user: IdportenUser = mockk()

    private val tokendingsExchange = mockk<TokendingsExchange>().also {
        coEvery { it.digisosToken(user) } returns "<accesstoken>"
    }

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
        val consumer = DigiSosConsumer(mockHttpClient, tokendingsExchange, digiSosEndpoint)

        val harInnsendte = runBlocking {
            consumer.harInnsendte(user)
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
        val consumer = DigiSosConsumer(mockHttpClient, tokendingsExchange, digiSosEndpoint)

        val harInnsendte = runBlocking {
            consumer.harInnsendte(user)
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
        val consumer = DigiSosConsumer(mockHttpClient, tokendingsExchange, digiSosEndpoint)

        shouldThrow<CommunicationException> {
            runBlocking {
                consumer.harInnsendte(user)
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
