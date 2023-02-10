package no.nav.personbruker.minesaker.api.saf.sakstemaer.transformers

import io.kotest.matchers.shouldBe
import no.nav.personbruker.minesaker.api.saf.sakstemaer.objectmothers.RelevantDatoObjectMother
import org.junit.jupiter.api.Test

internal class RelevantDatoTransformerTest {

    @Test
    fun `Skal transformere til intern modell, og plukke ut den datoen som er nyest`() {
        val datoer = RelevantDatoObjectMother.giveMeOneOfEachEkspederRegistertAndOpprettet()

        val nyesteDato = datoer.toInternal()

        nyesteDato shouldBe RelevantDatoObjectMother.giveMeDatoForUtgaaendeDokument().toInternal()
    }

}
