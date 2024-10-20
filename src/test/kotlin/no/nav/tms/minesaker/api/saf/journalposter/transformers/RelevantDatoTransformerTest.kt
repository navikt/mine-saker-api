package no.nav.tms.minesaker.api.saf.journalposter.transformers

import io.kotest.matchers.shouldBe
import no.nav.tms.minesaker.api.saf.journalposter.RelevantDatoTestData
import no.nav.tms.minesaker.api.saf.journalposter.v1.toInternal
import org.junit.jupiter.api.Test

internal class RelevantDatoTransformerTest {

    @Test
    fun `Skal transformere til intern modell, og plukke ut den datoen som er nyest`() {
        val datoer = listOf(
            RelevantDatoTestData.datoForInngaaendeDokument(),
            RelevantDatoTestData.datoForUtgaaendeDokument(),
            RelevantDatoTestData.datoForNotat()
        )

        val nyesteDato = datoer.toInternal()

        nyesteDato shouldBe RelevantDatoTestData.datoForNotat().toInternal()
    }

}
