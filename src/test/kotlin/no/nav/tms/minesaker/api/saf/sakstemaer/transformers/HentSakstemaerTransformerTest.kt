package no.nav.tms.minesaker.api.saf.sakstemaer.transformers

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import no.nav.tms.minesaker.api.config.InnsynsUrlResolver
import no.nav.tms.minesaker.api.saf.sakstemaer.SakstemaTestData
import org.junit.jupiter.api.Test

internal class HentSakstemaerTransformerTest {
    private val dummyResolver = InnsynsUrlResolver(mapOf(), "http://dummy.innsyn.no")

    @Test
    fun `Skal kunne transformere fra ekstern til intern modell`() {
        val external = SakstemaTestData.sakstema()

        val internal = external.toInternal(dummyResolver)

        internal.navn shouldBe external.navn
        internal.kode.toString() shouldBe external.kode
        internal.sistEndret.shouldNotBeNull()
    }

    @Test
    fun `Skal returnerer null hvis ingen relevant dato finnes`() {
        val external = SakstemaTestData.sakstema(journalposter = emptyList())

        val internal = external.toInternal(dummyResolver)

        internal.sistEndret.shouldBeNull()
    }

}
