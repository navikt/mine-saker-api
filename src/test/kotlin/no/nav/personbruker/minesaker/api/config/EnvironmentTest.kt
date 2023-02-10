package no.nav.personbruker.minesaker.api.config

import io.kotest.extensions.system.withEnvironment
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
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

            result.isFailure shouldBe true
            result.exceptionOrNull().shouldBeInstanceOf<IllegalStateException>()
        }
    }

    @Test
    fun `Skal kunne avgjore om appen kjorer i prod`() {
        withEnvironment(envVars) {
            val prod = Environment(clusterName = "prod-gcp")

            prod.isRunningInProd() shouldBe true
            prod.isRunningInDev() shouldBe false
        }
    }

    @Test
    fun `Skal kunne avgjore om appen kjorer i dev`() {
        withEnvironment(envVars) {
            val dev = Environment(clusterName = "dev-gcp")

            dev.isRunningInProd() shouldBe false
            dev.isRunningInDev() shouldBe true
        }
    }

    @Test
    fun `Hvis ukjent cluster er definert, saa skal det tolkes som dev`() {
        withEnvironment(envVars) {
            val ugyldig = Environment(clusterName = "ugyldig")

            ugyldig.isRunningInProd() shouldBe false
            ugyldig.isRunningInDev() shouldBe true
        }
    }

}
