package no.nav.personbruker.minesaker.api.digisos

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.minesaker.api.config.buildJsonSerializer
import no.nav.personbruker.minesaker.api.config.enableMineSakerJsonConfig
import no.nav.personbruker.minesaker.api.domain.Fodselsnummer
import no.nav.personbruker.minesaker.api.domain.ForenkletSakstema
import no.nav.personbruker.minesaker.api.sak.Kildetype
import no.nav.personbruker.minesaker.api.tokenx.AccessToken
import org.amshove.kluent.*
import org.junit.jupiter.api.Test

import java.net.URL

internal class DigiSosConsumerTest {

    private val objectMapper = jacksonObjectMapper().enableMineSakerJsonConfig()
    private val digiSosDummyEndpoint = URL("https://www.dummy.no")
    private val dummyToken = AccessToken("<access_token>")
    private val dummyIdent = Fodselsnummer("123")

    @Test
    fun `Skal kunne hente sakstemaer`() {
        val externalResponse = DigiSosResponseObjectMother.giveMeResponseAsList()
        val responseAsJson = objectMapper.writeValueAsString(externalResponse)
        val mockHttpClient = createMockHttpClient {
            respond(
                responseAsJson,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val consumer = DigiSosConsumer(mockHttpClient, digiSosEndpoint = digiSosDummyEndpoint)

        val internalSakstema = runBlocking {
            consumer.hentSakstemaer(dummyToken)
        }

        internalSakstema.results().size `should be equal to` externalResponse.size
        internalSakstema.results()[0] `should be instance of` ForenkletSakstema::class
        internalSakstema.results()[0].navn.value `should be equal to` externalResponse[0].navn
        internalSakstema.results()[0].kode.toString() `should be equal to` externalResponse[0].kode
        internalSakstema `should not be equal to` externalResponse
    }

    @Test
    fun `Hvis henting av sakstema feiler, saa skal det returneres et tomt resultat med info om at DigiSos feilet`() {
        val invalidJsonResponseSomVilTriggeEnException = "invalid response"
        val mockHttpClient = createMockHttpClient {
            respond(
                invalidJsonResponseSomVilTriggeEnException,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val consumer = DigiSosConsumer(mockHttpClient, digiSosEndpoint = digiSosDummyEndpoint)

        val sakstemarespons = runBlocking {
            consumer.hentSakstemaer(dummyToken)
        }

        sakstemarespons.hasErrors() `should be equal to` true
        sakstemarespons.results().shouldBeEmpty()
        sakstemarespons.errors() `should contain` Kildetype.DIGISOS
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
