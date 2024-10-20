package no.nav.tms.minesaker.api.digisos

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.serialization.*
import no.nav.tms.minesaker.api.setup.CommunicationException
import no.nav.tms.minesaker.api.saf.InnsynsUrlResolver
import no.nav.tms.minesaker.api.saf.sakstemaer.SakstemaResult
import java.net.URL
import java.util.*

class DigiSosConsumer(
    private val httpClient: HttpClient,
    private val digiSosEndpoint: URL,
    private val innsynsUrlResolver: InnsynsUrlResolver
) {

    private val log = KotlinLogging.logger {}
    private val callIdHeaderName = "Nav-Callid"

    suspend fun hentSakstemaer(accessToken: String): SakstemaResult {
        hent(accessToken).let { response ->
            if (!response.status.isSuccess()) {
                throw CommunicationException("Klarte ikke hente data fra digisos. Http-status [${response.status}]")
            }

            return unpackResponse(response)
                .toInternal(innsynsUrlResolver)
                .let { SakstemaResult(it) }
        }
    }

    private suspend fun unpackResponse(response: HttpResponse): List<DigiSosResponse> = try {
        response.body()
    } catch (e: JsonConvertException) {
        throw CommunicationException("Uventet form på json i svar fra Digisos.", e)
    }

    private suspend fun hent(accessToken: String): HttpResponse = withContext(Dispatchers.IO) {
        val callId = UUID.randomUUID()
        log.info { "Gjør kall mot DígiSos med correlationId=$callId" }
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
