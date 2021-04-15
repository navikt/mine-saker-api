package no.nav.personbruker.minesaker.api.saf.common.transformers

import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

internal class ZonedDateTimeTransformerTest {

    @Test
    fun `Skal haandtere at to datoer er like`() {
        val expectedDato = ZonedDateTime.now()
        val toLikeDatoer = listOf(expectedDato, expectedDato)

        val nyesteDato = toLikeDatoer.finnSistEndret()

        nyesteDato `should be equal to` expectedDato
    }

}
