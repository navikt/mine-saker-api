package no.nav.personbruker.minesaker.api.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.jackson.*

object HttpClientBuilder {

    fun build(): HttpClient = HttpClient(Apache) {
        install(ContentNegotiation) {
            jackson {
                jsonConfig()
            }
        }
        install(HttpTimeout)
    }

}

fun ObjectMapper.jsonConfig(): ObjectMapper {
    registerKotlinModule()
    registerModule(JavaTimeModule())
    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    return this
}
