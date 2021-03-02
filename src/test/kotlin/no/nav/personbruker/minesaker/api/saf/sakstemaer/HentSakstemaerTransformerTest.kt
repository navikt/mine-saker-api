package no.nav.personbruker.minesaker.api.saf.sakstemaer

import org.amshove.kluent.`should be empty`
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test

internal class HentSakstemaerTransformerTest {

    @Test
    fun `Skal kunne transformere fra ekstern til intern modell - Hent saker`() {
        val external = SakstemaObjectMother.giveMeOneSakstema()

        val internal = external.toInternal()

        internal.navn.value `should be equal to` external.navn
        internal.kode `should be equal to` external.kode
        internal.journalposter.`should be empty`()
    }

    @Test
    fun `Skal kunne transformere fra flere eksterne til interne - Hent saker`() {
        val externals = SakstemaObjectMother.giveMeListOfSakstema()

        val internals = HentSakstemaerTransformer.toInternal(externals)

        internals.size `should be equal to` externals.size
        internals.forEach { internal ->
            internal.shouldNotBeNull()
        }
    }

}
