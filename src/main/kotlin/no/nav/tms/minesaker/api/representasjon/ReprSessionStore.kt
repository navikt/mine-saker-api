package no.nav.tms.minesaker.api.representasjon

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.valkey.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.tms.common.util.config.IntEnvVar
import no.nav.tms.common.util.config.StringEnvVar.getEnvVar

interface ReprSessionStore {
    suspend fun setRepresentert(ident: String, representert: Representert)
    suspend fun getCurrentRepresentert(ident: String): Representert?
    suspend fun clearRepresentert(ident: String)
}

class ReprValkey(
    host: String = getEnvVar("VALKEY_HOST_FULLMAKT"),
    port: Int = IntEnvVar.getEnvVarAsInt("VALKEY_PORT_FULLMAKT"),
    username: String = getEnvVar("VALKEY_USERNAME_FULLMAKT"),
    password: String = getEnvVar("VALKEY_PASSWORD_FULLMAKT")
) : ReprSessionStore {

    private val objectMapper = jacksonObjectMapper()
    private val oneHourInSeconds = 3600L

    private val pool = run {
        val poolConfig = JedisPoolConfig().also {
            it.maxTotal = 32
            it.maxIdle = 32
            it.minIdle = 16
        }

        val clientConfig = DefaultJedisClientConfig.builder()
            .user(username)
            .password(password)
            .ssl(true)
            .build()

        val uri = HostAndPort(host, port)

        JedisPool(poolConfig, uri, clientConfig)
    }

    override suspend fun setRepresentert(ident: String, representert: Representert): Unit = withClient { client ->
        client.setex(ident, oneHourInSeconds, representert.toJson())
    }

    override suspend fun getCurrentRepresentert(ident: String): Representert? = withClient { client ->
        client.get(ident)
            ?.fullmaktGiverFromJson()
    }

    override suspend fun clearRepresentert(ident: String): Unit = withClient { client ->
        client.del(ident)
    }

    fun closeConnection() {
        pool.close()
    }

    private suspend fun <T> withClient(block: (Jedis) -> T): T = withContext(Dispatchers.IO) {
        pool.resource.use {
            block(it)
        }
    }

    private fun Representert.toJson() = objectMapper.writeValueAsString(this)

    private fun String.fullmaktGiverFromJson() = objectMapper.readValue(this, Representert::class.java)
}
