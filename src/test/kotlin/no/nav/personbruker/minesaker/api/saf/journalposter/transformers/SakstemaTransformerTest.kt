package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers.SakstemaObjectMother
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should not be empty`
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test

internal class SakstemaTransformerTest {

    @Test
    fun `Skal kunne transformere fra ekstern til intern modell - Hent konkret sakstema`() {
        val external = SakstemaObjectMother.giveMeSakstemaWithInngaaendeDokument()

        val internal = SakstemaTransformer.toInternal(external)

        internal.navn `should be equal to` external.navn
        internal.kode `should be equal to` external.kode
        internal.journalposter.`should not be empty`()
        internal.journalposter.size `should be equal to` 1
    }

    @Test
    fun `Skal kunne transformere fra flere eksterne til interne - Hent konkret sakstema`() {
        val externals = SakstemaObjectMother.giveMeListOfSakstemaer()

        val internals = SakstemaTransformer.toInternal(externals)

        internals.size `should be equal to` externals.size
        internals.forEach { internal ->
            internal.shouldNotBeNull()
        }
    }

}
