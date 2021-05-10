package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.personbruker.minesaker.api.domain.Fodselsnummer
import no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers.SakstemaObjectMother
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should not be empty`
import org.junit.jupiter.api.Test

internal class SakstemaTransformerTest {

    private val dummyIdent = Fodselsnummer("123")

    @Test
    fun `Skal kunne transformere fra ekstern til intern modell - Hent konkret sakstema`() {
        val external = SakstemaObjectMother.giveMeSakstemaWithInngaaendeDokument()

        val internal = external.toInternal(dummyIdent)

        internal.navn.value `should be equal to` external.navn
        internal.kode.toString() `should be equal to` external.kode.toString()
        internal.journalposter.`should not be empty`()
        internal.journalposter.size `should be equal to` 1
    }

}
