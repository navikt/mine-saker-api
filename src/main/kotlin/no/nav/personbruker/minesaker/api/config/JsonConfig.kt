package no.nav.personbruker.minesaker.api.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.client.features.json.*

fun buildJsonSerializer(): JacksonSerializer {
    return JacksonSerializer {
        enableMineSakerJsonConfig()
    }
}

fun ObjectMapper.enableMineSakerJsonConfig(): ObjectMapper {
    registerKotlinModule()
    registerModule(JavaTimeModule())
    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    return this
}
