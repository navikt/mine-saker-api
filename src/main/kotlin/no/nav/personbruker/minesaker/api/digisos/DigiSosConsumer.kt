package no.nav.personbruker.minesaker.api.digisos

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.personbruker.minesaker.api.common.exception.CommunicationException
import no.nav.personbruker.minesaker.api.saf.SafConsumer
import no.nav.personbruker.minesaker.api.sak.Kildetype
import no.nav.personbruker.minesaker.api.sak.SakstemaResult
import no.nav.personbruker.minesaker.api.tokenx.AccessToken
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*

class DigiSosConsumer(
    private val httpClient: HttpClient,
    private val digiSosEndpoint: URL
) {

    private val log = LoggerFactory.getLogger(SafConsumer::class.java)

    private val callIdHeaderName = "Nav-Callid"

    suspend fun hentSakstemaer(accessToken: AccessToken): SakstemaResult {
        return try {
            val responseDto: List<DigiSosResponse> = hent(accessToken)
            SakstemaResult(responseDto.toInternal())

        } catch (e: Exception) {
            log.warn("Klarte ikke å hente data fra DigiSos, returnerer et resultat med info om at det feilet mot DigiSos: $e", e)
            SakstemaResult(errors = listOf(Kildetype.DIGISOS))
        }
    }

    private suspend inline fun <reified T> hent(accessToken: AccessToken): T =
        runCatching<T> {
            val callId = UUID.randomUUID()
            log.info("Gjør kall mot DígiSos med correlationId=$callId")
            withContext(Dispatchers.IO) {
                httpClient.get {
                    url("$digiSosEndpoint/minesaker/innsendte")
                    method = HttpMethod.Get
                    header(callIdHeaderName, callId)
                    header(HttpHeaders.Authorization, "Bearer ${accessToken.value}")
                    contentType(ContentType.Application.Json)
                    accept(ContentType.Application.Json)
                }
            }
        }.onFailure { cause ->
            throw CommunicationException("Klarte ikke å hente data fra DigiSos", cause)
        }.getOrThrow()

}
