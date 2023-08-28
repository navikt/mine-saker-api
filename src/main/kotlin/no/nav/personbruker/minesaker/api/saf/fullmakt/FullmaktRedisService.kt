package no.nav.personbruker.minesaker.api.saf.fullmakt

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import no.nav.personbruker.dittnav.common.util.config.StringEnvVar.getEnvVar

class FullmaktRedisService(
    host: String = getEnvVar("REDIS_URI_FULLMAKT"),
    username: String = getEnvVar("REDIS_USERNAME_FULLMAKT"),
    password: String = getEnvVar("REDIS_PASSWORD_FULLMAKT")
) {
    private val oneHourInSeconds = 3600L

    private val commands = RedisURI.builder(RedisURI.create(host))
        .withAuthentication(username, password)
        .build()
        .let { RedisClient.create(it) }
        .connect()
        .sync()

    private val objectMapper = jacksonObjectMapper()

    fun setForhold(subject: String, validForhold: ValidForhold) {
        commands.setex(subject, oneHourInSeconds, validForhold.toJson())
    }

    fun getForhold(subject: String): ValidForhold? {
        return commands.get(subject)
            ?.validForholdFromJson()
    }

    fun clearForhold(subject: String) {
        commands.del(subject)
    }

    private fun ValidForhold.toJson() = objectMapper.writeValueAsString(this)

    private fun String.validForholdFromJson(): ValidForhold = objectMapper.readValue(this, ValidForhold::class.java)
}
