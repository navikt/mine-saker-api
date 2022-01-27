package no.nav.personbruker.minesaker.api.config

import io.ktor.client.HttpClient
import io.ktor.client.features.timeout
import io.ktor.client.request.*
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser
import java.net.URL

suspend inline fun <reified T> HttpClient.get(url: URL, user: IdportenUser): T = withContext(Dispatchers.IO) {
    request {
        url(url)
        method = HttpMethod.Get
        header(HttpHeaders.Authorization, user.createAuthenticationHeader())
    }
}

suspend inline fun <reified T> HttpClient.getExtendedTimeout(url: URL, user: IdportenUser): T = withContext(Dispatchers.IO) {
    request {
        url(url)
        method = HttpMethod.Get
        header(HttpHeaders.Authorization, user.createAuthenticationHeader())
        timeout {
            socketTimeoutMillis = 30000
            connectTimeoutMillis = 10000
            requestTimeoutMillis = 40000
        }
    }
}
