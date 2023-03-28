package no.nav.personbruker.minesaker.api.config

import io.kotest.extensions.system.withEnvironment
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Test

internal class EnvironmentTest {

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

}
