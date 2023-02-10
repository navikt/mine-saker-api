package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import io.kotest.matchers.shouldBe
import no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers.RelevantDatoObjectMother
import org.junit.jupiter.api.Test

internal class RelevantDatoTransformerTest {

    @Test
    fun `Skal transformere til intern modell, og plukke ut den datoen som er nyest`() {
        val datoer = RelevantDatoObjectMother.giveMeOneOfEachEkspederRegistertAndOpprettet()

        val nyesteDato = datoer.toInternal()

        nyesteDato shouldBe RelevantDatoObjectMother.giveMeDatoForNotat().toInternal()
    }

}
