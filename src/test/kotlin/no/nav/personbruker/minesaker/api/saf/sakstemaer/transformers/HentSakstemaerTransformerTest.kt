package no.nav.personbruker.minesaker.api.saf.sakstemaer.transformers

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import no.nav.personbruker.minesaker.api.saf.sakstemaer.objectmothers.SakstemaObjectMother
import org.junit.jupiter.api.Test

internal class HentSakstemaerTransformerTest {

    @Test
    fun `Skal kunne transformere fra ekstern til intern modell`() {
        val external = SakstemaObjectMother.giveMeOneSakstema()

        val internal = external.toInternal()

        internal.navn shouldBe external.navn
        internal.kode.toString() shouldBe external.kode
        internal.sistEndret.shouldNotBeNull()
    }

    @Test
    fun `Skal returnerer null hvis ingen relevant dato finnes`() {
        val external = SakstemaObjectMother.giveMeOneSakstema(journalposter = emptyList())

        val internal = external.toInternal()

        internal.sistEndret.shouldBeNull()
    }

}
