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
import io.mockk.every
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
    private val digiSosEndpoint = createUrl("https://dummy")
    private val legacyDigiSosEndpoint = createUrl("https://legacy.dummy")

    private val userToken = "<original>"

    private val user: IdportenUser = mockk<IdportenUser>().also {
        every { it.tokenString } returns userToken
    }

    private val tokendingsExchange = mockk<TokendingsExchange>().also {
        coEvery { it.digisosToken(userToken) } returns "<exchanged>"
        coEvery { it.legacyDigisosToken(userToken) } returns "<exchanged>"
    }

    @Test
    fun `returnerer true hvis bruker har noen innsendte søknader i ny tjeneste`() {
        val externalResponse = listOf(responseSisteEndretEnUkeSiden())
        val responseAsJson = objectMapper.writeValueAsString(externalResponse)
        val mockHttpClient = createMockHttpClient(handler = {
            respond(
                responseAsJson,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        })

        val consumer = DigiSosConsumer(mockHttpClient, tokendingsExchange, digiSosEndpoint, legacyDigiSosEndpoint)

        val harInnsendte = runBlocking {
            consumer.harInnsendte(user)
        }

        harInnsendte shouldBe true
    }
    @Test
    fun `returnerer true hvis bruker har noen innsendte søknader i gammel tjeneste`() {
        val externalResponse = listOf(responseSisteEndretEnUkeSiden())
        val responseAsJson = objectMapper.writeValueAsString(externalResponse)
        val mockHttpClient = createMockHttpClient(legacyHandler = {
            respond(
                responseAsJson,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        })

        val consumer = DigiSosConsumer(mockHttpClient, tokendingsExchange, digiSosEndpoint, legacyDigiSosEndpoint)

        val harInnsendte = runBlocking {
            consumer.harInnsendte(user)
        }

        harInnsendte shouldBe true
    }

    @Test
    fun `returnerer false hvis bruker ikke har innsendte søknader`() {
        val mockHttpClient = createMockHttpClient()

        val consumer = DigiSosConsumer(mockHttpClient, tokendingsExchange, digiSosEndpoint, legacyDigiSosEndpoint)

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
        val consumer = DigiSosConsumer(mockHttpClient, tokendingsExchange, digiSosEndpoint, legacyDigiSosEndpoint)

        shouldThrow<CommunicationException> {
            runBlocking {
                consumer.harInnsendte(user)
            }
        }

    }

    private fun createMockHttpClient(
        handler: MockRequestHandleScope.() -> HttpResponseData = { emptyResponse() },
        legacyHandler: MockRequestHandleScope.() -> HttpResponseData = { emptyResponse() }
    ): HttpClient {

        return HttpClient(MockEngine) {
            engine {
                addHandler {
                    when (it.url.host) {
                        digiSosEndpoint.host -> handler()
                        legacyDigiSosEndpoint.host -> legacyHandler()
                        else -> respond("not found", status = HttpStatusCode.NotFound)
                    }
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

private fun MockRequestHandleScope.emptyResponse() = respond(
    "[]",
    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
)

private fun responseSisteEndretEnUkeSiden() = DigiSosResponse(
    "Økonomisk sosialhjelp",
    "KOM",
    LocalDateTime.now().minusWeeks(1)
)
