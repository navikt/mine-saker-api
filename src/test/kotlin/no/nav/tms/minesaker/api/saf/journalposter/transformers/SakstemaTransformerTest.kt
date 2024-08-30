package no.nav.tms.minesaker.api.saf.journalposter.transformers

import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import no.nav.tms.minesaker.api.saf.journalposter.JournalpostTestData.sakstemaWithInngaaendeDokument
import no.nav.tms.minesaker.api.saf.journalposter.v1.toInternal
import org.junit.jupiter.api.Test

internal class SakstemaTransformerTest {

    private val dummyIdent = "123"

    @Test
    fun `Skal kunne transformere fra ekstern til intern modell - Hent konkret sakstema`() {
        val external = sakstemaWithInngaaendeDokument()

        val internal = external.toInternal(dummyIdent)

        internal.navn shouldBe external.navn
        internal.kode.toString() shouldBe external.kode
        internal.journalposter.shouldNotBeEmpty()
        internal.journalposter.size shouldBe 1
    }

}
