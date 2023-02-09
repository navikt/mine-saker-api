package no.nav.personbruker.minesaker.api.config

import io.kotest.extensions.system.withEnvironment
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class NaisEnvironmentTest {

    @Test
    fun `Skal skunne identifisere at appen kjorer i prod-klusteret paa GCP`() {
        withEnvironment("NAIS_CLUSTER_NAME" to "prod-gcp") {
            NaisEnvironment.isRunningInProd() shouldBe true
            NaisEnvironment.isRunningInDev() shouldBe false
        }
    }

    @Test
    fun `Skal skunne identifisere at appen kjorer i et dev-kluster`() {
        withEnvironment("NAIS_CLUSTER_NAME" to "dev-gcp") {
            NaisEnvironment.isRunningInProd() shouldBe false
            NaisEnvironment.isRunningInDev() shouldBe true
        }
    }

    @Test
    fun `Kjorende miljo skal identifiseres som dev hvis NAIS_CLUSTER_NAME er satt til null`() {
        withEnvironment("NAIS_CLUSTER_NAME" to null) {
            NaisEnvironment.isRunningInProd() shouldBe false
            NaisEnvironment.isRunningInDev() shouldBe true
        }
    }

    @Test
    fun `Kjorende miljo skal identifiseres som dev hvis NAIS_CLUSTER_NAME har ugyldig verdi`() {
        withEnvironment("NAIS_CLUSTER_NAME" to "ugyldig verdi") {
            NaisEnvironment.isRunningInProd() shouldBe false
            NaisEnvironment.isRunningInDev() shouldBe true
        }
    }

    @Test
    fun `Hvis appen ikke kjores i et prod-kluster, saa skal det identifiseres som at appen kjorer i dev`() {
        NaisEnvironment.isRunningInProd() shouldBe false
        NaisEnvironment.isRunningInDev() shouldBe true
    }

}
