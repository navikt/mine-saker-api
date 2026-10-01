package no.nav.tms.minesaker.api.representasjon

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.tms.minesaker.api.setup.TokendingsExchange
import no.nav.tms.token.support.user.token.verification.UserPrincipal

class ReprConsumer(
    private val httpClient: HttpClient,
    private val tokendingsExchange: TokendingsExchange,
    private val pdlFullmaktUrl: String
) {
    suspend fun getRepresenterte(user: UserPrincipal): KanRepresentere {
        return getRepresenterte(tokendingsExchange.pdlFullmaktToken(user.accessToken))
    }

    private suspend fun getRepresenterte(accessToken: String): KanRepresentere =
        withContext(Dispatchers.IO) {
            httpClient.get {
                url("$pdlFullmaktUrl/api/v2/eksternbruker/kan-representere")
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

    suspend fun token(user: UserPrincipal): String = tokendingsExchange.pdlFullmaktToken(user.accessToken)
}

data class KanRepresentere(
    val fullmakt: List<Fullmaktsgiver>,
    val vergemaal: List<Verge>
) {
    data class Fullmaktsgiver(
        val fullmaktsgiver: String
    )

    data class Verge(
        val verge: String
    )
}
