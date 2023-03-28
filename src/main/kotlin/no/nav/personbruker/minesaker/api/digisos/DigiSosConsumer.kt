package no.nav.personbruker.minesaker.api.digisos

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import no.nav.personbruker.minesaker.api.common.exception.CommunicationException
import no.nav.personbruker.minesaker.api.config.InnsynsUrlResolver
import no.nav.personbruker.minesaker.api.sak.Kildetype
import no.nav.personbruker.minesaker.api.sak.SakstemaResult
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
        return try {
            hent(accessToken).let { response ->
                if (!response.status.isSuccess()) {
                    throw CommunicationException("Klarte ikke hente data fra digisos. Http-status [${response.status}]")
                }
                val responseElements: List<DigiSosResponse> = response.body()
                return SakstemaResult(responseElements.toInternal(innsynsUrlResolver))
            }

        } catch (e: Exception) {
            log.warn(
                "Klarte ikke å hente data fra DigiSos, returnerer et resultat med info om at det feilet mot DigiSos: $e",
                e
            )
            SakstemaResult(errors = listOf(Kildetype.DIGISOS))
        }
    }

    private suspend fun hent(accessToken: String): HttpResponse = withContext(Dispatchers.IO) {
        val callId = UUID.randomUUID()
        log.info("Gjør kall mot DígiSos med correlationId=$callId")
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
