package no.nav.personbruker.minesaker.api.saf.sakstemaer

import org.amshove.kluent.`should be empty`
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

internal class HentSakstemaerTransformerTest {

    @Test
    fun `Skal kunne transformere fra ekstern til intern modell - Hent saker`() {
        val external = SakstemaObjectMother.giveMeOneSakstema()

        val internal = external.toInternal()

        internal.navn.value `should be equal to` external.navn
        internal.kode.toString() `should be equal to` external.kode.toString()
        internal.journalposter.`should be empty`()
    }

}
