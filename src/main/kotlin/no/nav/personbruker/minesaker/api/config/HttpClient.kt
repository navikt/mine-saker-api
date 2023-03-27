package no.nav.personbruker.minesaker.api.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.client.HttpClient
import io.ktor.client.call.*
import io.ktor.client.engine.apache.Apache
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser
import java.net.URL

object HttpClientBuilder {

    fun build(): HttpClient {
        return config()
    }

    fun config() = HttpClient(Apache) {
        install(ContentNegotiation) {
            jackson {
                enableMineSakerJsonConfig()
            }
        }
        install(HttpTimeout)
    }

}

fun ObjectMapper.enableMineSakerJsonConfig(): ObjectMapper {
    registerKotlinModule()
    registerModule(JavaTimeModule())
    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    return this
}

suspend inline fun <reified T> HttpClient.get(url: URL, user: IdportenUser): T = withContext(Dispatchers.IO) {
    request {
        url(url)
        method = HttpMethod.Get
        header(HttpHeaders.Authorization, user.createAuthenticationHeader())
    }.body()
}
