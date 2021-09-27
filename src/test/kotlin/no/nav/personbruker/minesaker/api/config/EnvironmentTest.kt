package no.nav.personbruker.minesaker.api.config

import io.kotest.extensions.system.withEnvironment
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be instance of`
import org.junit.jupiter.api.Test

internal class EnvironmentTest {

    private val envVars = mapOf(
        "CORS_ALLOWED_ORIGINS" to ".dev.nav.no",
        "SAF_API_URL" to "https://dummy/saf",
        "SAF_CLIENT_ID" to "saf dummy client id",
        "DIGISOS_API_URL" to "https://dummy/digisos",
        "DIGISOS_CLIENT_ID" to "digisos dummy client id",
        "POST_LOGOUT_URL" to "https://dev.nav.no",
        "NAIS_CLUSTER_NAME" to "localhost"
    )

    @Test
    fun `Det skal kastes en feil hvis miljovariablene ikke er satt`() {
        withEnvironment(emptyMap()) {

            val result = runCatching {
                Environment()
            }

            result.isFailure `should be equal to` true
            result.exceptionOrNull() `should be instance of` IllegalArgumentException::class
        }
    }

    @Test
    fun `Skal kunne avgjore om appen kjorer i prod`() {
        withEnvironment(envVars) {
            val prod = Environment(clusterName = "prod-gcp")

            prod.isRunningInProd() `should be equal to` true
            prod.isRunningInDev() `should be equal to` false
        }
    }

    @Test
    fun `Skal kunne avgjore om appen kjorer i dev`() {
        withEnvironment(envVars) {
            val dev = Environment(clusterName = "dev-gcp")

            dev.isRunningInProd() `should be equal to` false
            dev.isRunningInDev() `should be equal to` true
        }
    }

    @Test
    fun `Hvis ukjent cluster er definert, saa skal det tolkes som dev`() {
        withEnvironment(envVars) {
            val ugyldig = Environment(clusterName = "ugyldig")

            ugyldig.isRunningInProd() `should be equal to` false
            ugyldig.isRunningInDev() `should be equal to` true
        }
    }

}
