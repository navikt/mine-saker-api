package no.nav.personbruker.minesaker.api.saf.sakstemaer.transformers

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import no.nav.personbruker.minesaker.api.config.InnsynsUrlResolver
import no.nav.personbruker.minesaker.api.saf.sakstemaer.objectmothers.SakstemaObjectMother
import org.junit.jupiter.api.Test
import java.net.URL

internal class HentSakstemaerTransformerTest {
    private val dummyResolver = InnsynsUrlResolver(mapOf(), URL("http://dummy.innsyn.no"))

    @Test
    fun `Skal kunne transformere fra ekstern til intern modell`() {
        val external = SakstemaObjectMother.giveMeOneSakstema()

        val internal = external.toInternal(dummyResolver)

        internal.navn shouldBe external.navn
        internal.kode.toString() shouldBe external.kode
        internal.sistEndret.shouldNotBeNull()
    }

    @Test
    fun `Skal returnerer null hvis ingen relevant dato finnes`() {
        val external = SakstemaObjectMother.giveMeOneSakstema(journalposter = emptyList())

        val internal = external.toInternal(dummyResolver)

        internal.sistEndret.shouldBeNull()
    }

}
