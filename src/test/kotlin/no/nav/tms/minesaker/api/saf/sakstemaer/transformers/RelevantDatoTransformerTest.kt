package no.nav.tms.minesaker.api.saf.sakstemaer.transformers

import io.kotest.matchers.shouldBe
import no.nav.tms.minesaker.api.saf.sakstemaer.SakstemaTestData
import no.nav.tms.minesaker.api.saf.sakstemaer.SakstemaTestData.datoForUtgaaendeDokument
import org.junit.jupiter.api.Test

internal class RelevantDatoTransformerTest {

    @Test
    fun `Skal transformere til intern modell, og plukke ut den datoen som er nyest`() {
        val datoer = listOf(
            SakstemaTestData.datoForInngaaendeDokument(),
            datoForUtgaaendeDokument(),
            SakstemaTestData.datoForNotat()
        )

        val nyesteDato = datoer.toInternal()

        nyesteDato shouldBe datoForUtgaaendeDokument().toInternal()
    }

}
