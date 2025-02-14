package no.nav.tms.minesaker.api.fullmakt

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.valkey.DefaultJedisClientConfig
import io.valkey.HostAndPort
import io.valkey.JedisPool
import io.valkey.JedisPoolConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.tms.common.util.config.IntEnvVar
import no.nav.tms.common.util.config.StringEnvVar.getEnvVar

interface FullmaktSessionStore {
    suspend fun setFullmaktGiver(ident: String, fullmaktGiver: FullmaktGiver)
    suspend fun getCurrentFullmaktGiver(ident: String): FullmaktGiver?
    suspend fun clearFullmaktGiver(ident: String)
}

class FullmaktValkey(
    host: String = getEnvVar("VALKEY_HOST_FULLMAKT"),
    port: Int = IntEnvVar.getEnvVarAsInt("VALKEY_PORT_FULLMAKT"),
    username: String = getEnvVar("VALKEY_USERNAME_FULLMAKT"),
    password: String = getEnvVar("VALKEY_PASSWORD_FULLMAKT")
) : FullmaktSessionStore {

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

    override suspend fun setFullmaktGiver(ident: String, fullmaktGiver: FullmaktGiver): Unit = withContext(Dispatchers.IO) {
        pool.resource.setex(ident, oneHourInSeconds, fullmaktGiver.toJson())
    }

    override suspend fun getCurrentFullmaktGiver(ident: String): FullmaktGiver? = withContext(Dispatchers.IO) {
        pool.resource.get(ident)
            ?.fullmaktGiverFromJson()
    }

    override suspend fun clearFullmaktGiver(ident: String): Unit = withContext(Dispatchers.IO) {
        pool.resource.del(ident)
    }

    fun closeConnection() {
        pool.close()
    }

    private fun FullmaktGiver.toJson() = objectMapper.writeValueAsString(this)

    private fun String.fullmaktGiverFromJson() = objectMapper.readValue(this, FullmaktGiver::class.java)
}
