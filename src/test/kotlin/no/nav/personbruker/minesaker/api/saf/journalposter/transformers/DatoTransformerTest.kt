package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import io.kotest.matchers.nulls.shouldNotBeNull
import no.nav.personbruker.minesaker.api.saf.journalposter.RelevantDatoTestData
import org.junit.jupiter.api.Test

internal class DatoTransformerTest {

    @Test
    fun `Skal transformere fra ekstern til intern modell`() {
        val external = RelevantDatoTestData.datoForInngaaendeDokument()

        val internal = external.toInternal()

        internal.shouldNotBeNull()
        internal.shouldNotBeNull()
    }

}
