package no.nav.personbruker.minesaker.api.saf

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.minesaker.api.common.exception.SafException
import no.nav.personbruker.minesaker.api.config.buildJsonSerializer
import no.nav.personbruker.minesaker.api.saf.dto.`in`.objectmother.SafResultWrapperObjectMother
import no.nav.personbruker.minesaker.api.saf.dto.out.Sakstema
import no.nav.personbruker.minesaker.api.saf.queries.HentKonkretSakstema
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be instance of`
import org.amshove.kluent.`should not be equal to`
import org.amshove.kluent.shouldHaveKey
import org.junit.jupiter.api.Test
import java.net.URL

internal class SafConsumerTest {

    private val objectMapper = jacksonObjectMapper()
    private val safDummyEndpoint = URL("https://www.dummy.no")
    private val sakstemaRequest = HentKonkretSakstema.createRequest("FOR")

    @Test
    internal fun `Skal kunne motta data fra SAF og transformere disse til intern DTO`() {
        val externalResponse = SafResultWrapperObjectMother.giveMeOneResult()
        val safResponseAsJson = objectMapper.writeValueAsString(externalResponse)
        val mockHttpClient = createMockHttpClient {
            respond(safResponseAsJson, headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()))
        }
        val safConsumerWithResponse = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint)

        val internalSakstema = runBlocking {
            safConsumerWithResponse.hentKonkretSakstema(sakstemaRequest)
        }

        val externalSakstema = externalResponse.data.dokumentoversiktSelvbetjening.tema
        internalSakstema.size `should be equal to` externalSakstema.size
        internalSakstema[0] `should be instance of` Sakstema::class
        internalSakstema[0].navn `should be equal to` externalSakstema[0].navn
        internalSakstema[0].kode `should be equal to` externalSakstema[0].kode
        internalSakstema `should not be equal to` externalSakstema
    }

    @Test
    internal fun `Skal takle feil mot SAF, lagre kontekst og kaste intern feil videre`() {
        val mockHttpClient = createMockHttpClient {
            respondError(HttpStatusCode.BadRequest)
        }

        val safConsumerSomFeiler = SafConsumer(mockHttpClient, safEndpoint = safDummyEndpoint)

        val result = runCatching {
            runBlocking {
                safConsumerSomFeiler.hentKonkretSakstema(sakstemaRequest)
            }
        }

        result.isFailure `should be equal to` true
        val receivedException = result.exceptionOrNull()
        receivedException `should be instance of` SafException::class
        val se = receivedException as SafException
        se.context.shouldHaveKey("query")
        se.context.shouldHaveKey("variables")
        se.context["query"] `should be equal to` sakstemaRequest.query
        se.context["variables"] `should be equal to` sakstemaRequest.variables
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
