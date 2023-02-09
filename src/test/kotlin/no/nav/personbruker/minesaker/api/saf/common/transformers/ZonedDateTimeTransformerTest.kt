package no.nav.personbruker.minesaker.api.saf.common.transformers

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

internal class ZonedDateTimeTransformerTest {

    @Test
    fun `Skal haandtere at to datoer er like`() {
        val expectedDato = ZonedDateTime.now()
        val toLikeDatoer = listOf(expectedDato, expectedDato)

        val nyesteDato = toLikeDatoer.finnSistEndret()

        nyesteDato shouldBe expectedDato
    }

}
