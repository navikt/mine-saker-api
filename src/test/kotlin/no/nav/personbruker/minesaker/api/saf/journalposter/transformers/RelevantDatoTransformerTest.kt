package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.personbruker.minesaker.api.saf.domain.Datotype
import no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers.RelevantDatoObjectMother
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test

internal class RelevantDatoTransformerTest {

    @Test
    fun `Skal transformere fra ekstern til intern modell`() {
        val external = RelevantDatoObjectMother.giveMeDatoForInngaaendeDokument()

        val internal = external.toInternal()

        internal.shouldNotBeNull()
        internal.dato.shouldNotBeNull()
        internal.type `should be equal to` Datotype.REGISTRERT
    }

}
