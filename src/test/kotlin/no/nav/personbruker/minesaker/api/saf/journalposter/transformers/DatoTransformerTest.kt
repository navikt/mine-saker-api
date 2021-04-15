package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers.RelevantDatoObjectMother
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test

internal class DatoTransformerTest {

    @Test
    fun `Skal transformere fra ekstern til intern modell`() {
        val external = RelevantDatoObjectMother.giveMeDatoForInngaaendeDokument()

        val internal = external.toInternal()

        internal.shouldNotBeNull()
        internal.shouldNotBeNull()
    }

}
