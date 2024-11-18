package no.nav.tms.minesaker.api.saf.fullmakt

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.tms.minesaker.api.setup.TokendingsExchange
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser

class FullmaktConsumer(
    private val httpClient: HttpClient,
    private val tokendingsExchange: TokendingsExchange,
    private val pdlFullmaktUrl: String
) {
    suspend fun getFullmaktsGivere(user: IdportenUser): List<FullmaktGiver> {
        return getFullmaktList(tokendingsExchange.pdlFullmaktToken(user))
            .map {
                FullmaktGiver(
                    ident = it.fullmaktsgiver,
                    navn = it.fullmaktsgiverNavn
                )
            }
    }

    private suspend fun getFullmaktList(accessToken: String): FullmaktResponse =
        withContext(Dispatchers.IO) {
            httpClient.get {
                url("$pdlFullmaktUrl/api/eksternbruker/fullmakt/fullmektig/tema")
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

    suspend fun token(user: IdportenUser): String = tokendingsExchange.pdlFullmaktToken(user)
}

typealias FullmaktResponse = List<FullmaktResponseEntry>

data class FullmaktResponseEntry(
    val fullmaktsgiver: String,
    val fullmaktsgiverNavn: String
)
