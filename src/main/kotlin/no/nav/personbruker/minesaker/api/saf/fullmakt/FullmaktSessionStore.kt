package no.nav.personbruker.minesaker.api.saf.fullmakt

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.personbruker.dittnav.common.util.config.StringEnvVar.getEnvVar

interface FullmaktSessionStore {
    suspend fun setFullmaktGiver(ident: String, fullmaktGiver: FullmaktGiver)
    suspend fun getCurrentFullmaktGiver(ident: String): FullmaktGiver?
    suspend fun clearFullmaktGiver(ident: String)
}

class FullmaktRedisService(
    host: String = getEnvVar("REDIS_URI_FULLMAKT"),
    username: String = getEnvVar("REDIS_USERNAME_FULLMAKT"),
    password: String = getEnvVar("REDIS_PASSWORD_FULLMAKT")
) : FullmaktSessionStore {
    private val oneHourInSeconds = 3600L

    private val commands = RedisURI.builder(RedisURI.create(host))
        .withAuthentication(username, password)
        .build()
        .let { RedisClient.create(it) }
        .connect()
        .sync()

    private val objectMapper = jacksonObjectMapper()

    override suspend fun setFullmaktGiver(ident: String, fullmaktGiver: FullmaktGiver): Unit = withContext(Dispatchers.IO) {
        commands.setex(ident, oneHourInSeconds, fullmaktGiver.toJson())
    }

    override suspend fun getCurrentFullmaktGiver(ident: String): FullmaktGiver? = withContext(Dispatchers.IO) {
        commands.get(ident)
            ?.fullmaktGiverFromJson()
    }

    override suspend fun clearFullmaktGiver(ident: String): Unit = withContext(Dispatchers.IO) {
        commands.del(ident)
    }

    private fun FullmaktGiver.toJson() = objectMapper.writeValueAsString(this)

    private fun String.fullmaktGiverFromJson() = objectMapper.readValue(this, FullmaktGiver::class.java)
}
