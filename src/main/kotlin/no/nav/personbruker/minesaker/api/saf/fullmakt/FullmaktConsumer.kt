package no.nav.personbruker.minesaker.api.saf.fullmakt

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import no.nav.personbruker.minesaker.api.config.TokendingsExchange
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser
import java.net.URL

class FullmaktConsumer(
    private val httpClient: HttpClient,
    private val tokendingsExchange: TokendingsExchange,
    private val pdlFullmaktUrl: String = "http://pdl-fullmakt-api.repr"
) {
    private val log = KotlinLogging.logger {}

    suspend fun getFullmaktForhold(user: IdportenUser): FullmaktForhold {
        return getFullmaktDetails(tokendingsExchange.pdlFullmaktToken(user))
            .let(FullmaktForhold::fromFullmaktDetails)
    }

    private suspend fun getFullmaktDetails(accessToken: String): List<FullmaktDetails> =
        withContext(Dispatchers.IO) {
            httpClient.get {
                url("$pdlFullmaktUrl/fullmektig")
                method = HttpMethod.Get
                header(HttpHeaders.Authorization, "Bearer $accessToken")
                accept(ContentType.Application.Json)
                timeout {
                    socketTimeoutMillis = 25000
                    connectTimeoutMillis = 10000
                    requestTimeoutMillis = 35000
                }
            }
        }.body()
}
