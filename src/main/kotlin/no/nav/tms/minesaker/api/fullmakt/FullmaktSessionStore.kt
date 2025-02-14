package no.nav.tms.minesaker.api.fullmakt

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.valkey.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.tms.common.util.config.StringEnvVar.getEnvVar

interface FullmaktSessionStore {
    suspend fun setFullmaktGiver(ident: String, fullmaktGiver: FullmaktGiver)
    suspend fun getCurrentFullmaktGiver(ident: String): FullmaktGiver?
    suspend fun clearFullmaktGiver(ident: String)
}

class FullmaktRedis(
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

    override suspend fun setFullmaktGiver(ident: String, fullmaktGiver: FullmaktGiver): Unit = withClient { client ->
        client.setex(ident, oneHourInSeconds, fullmaktGiver.toJson())
    }

    override suspend fun getCurrentFullmaktGiver(ident: String): FullmaktGiver? = withClient { client ->
        client.get(ident)
            ?.fullmaktGiverFromJson()
    }

    override suspend fun clearFullmaktGiver(ident: String): Unit = withClient { client ->
        client.del(ident)
    }

    fun closeConnection() {
        pool.close()
    }

    private suspend fun <T> withClient(block: (Jedis) -> T): T = withContext(Dispatchers.IO) {
        val jedis = pool.resource

        try {
            block(jedis)
        } finally {
            pool.returnResource(jedis)
        }
    }

    private fun FullmaktGiver.toJson() = objectMapper.writeValueAsString(this)

    private fun String.fullmaktGiverFromJson() = objectMapper.readValue(this, FullmaktGiver::class.java)
}
