package no.nav.personbruker.minesaker.api.saf

import no.nav.personbruker.minesaker.api.saf.dto.`in`.Sakstema
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test

internal class SakstemaTransformerTest {

    @Test
    internal fun `Skal kunne transformere fra ekstern til intern modell`() {
        val external = Sakstema("navn", "kode")

        val internal = SakstemaTransformer.toInternal(external)

        internal.navn `should be equal to` external.navn
        internal.kode `should be equal to` external.kode
    }

    @Test
    internal fun `Skal kunne transformere fra flere eksterne til interne`() {
        val externals = listOf(Sakstema("navn1", "kode1"), Sakstema("navn2", "kode2"))

        val internals = SakstemaTransformer.toInternal(externals)

        internals.size `should be equal to` externals.size
        internals.forEach { internal ->
            internal.shouldNotBeNull()
        }
    }

}
