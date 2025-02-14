package no.nav.tms.minesaker.api.innsendte

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.serialization.*
import kotlinx.coroutines.*
import no.nav.tms.minesaker.api.setup.CommunicationException
import no.nav.tms.minesaker.api.setup.TokendingsExchange
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser
import java.net.URL
import java.time.LocalDateTime
import java.util.*

class DigiSosConsumer(
    private val httpClient: HttpClient,
    private val tokendingsExchange: TokendingsExchange,
    private val digiSosEndpoint: URL,
) {

    private val log = KotlinLogging.logger {}
    private val callIdHeaderName = "Nav-Callid"

    suspend fun harInnsendte(user: IdportenUser): Boolean {
        val accessToken = tokendingsExchange.digisosToken(user)

        hentInnsendte(accessToken).let { response ->
            if (!response.status.isSuccess()) {
                throw CommunicationException("Klarte ikke hente data fra digisos. Http-status [${response.status}]")
            }

            return unpackResponse(response)
                .isNotEmpty()
        }
    }


    private suspend fun unpackResponse(response: HttpResponse): List<DigiSosResponse> = try {
        response.body()
    } catch (e: JsonConvertException) {
        throw CommunicationException("Uventet form på json i svar fra Digisos.", e)
    }

    private suspend fun hentInnsendte(accessToken: String): HttpResponse = withContext(Dispatchers.IO) {
        val callId = UUID.randomUUID()
        log.info { "Gjør kall mot DigiSos ($digiSosEndpoint) med callId: $callId" }
        httpClient.get {
            url("$digiSosEndpoint/minesaker/innsendte")
            method = HttpMethod.Get
            header(callIdHeaderName, callId)
            header(HttpHeaders.Authorization, "Bearer $accessToken")
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }
    }
}

data class DigiSosResponse(
    val navn : String,
    val kode : String,
    val sistEndret : LocalDateTime
)
