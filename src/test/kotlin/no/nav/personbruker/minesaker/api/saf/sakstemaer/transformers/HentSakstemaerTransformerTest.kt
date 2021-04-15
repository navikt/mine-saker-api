package no.nav.personbruker.minesaker.api.saf.sakstemaer.transformers

import no.nav.personbruker.minesaker.api.common.exception.TransformationException
import no.nav.personbruker.minesaker.api.saf.sakstemaer.objectmothers.SakstemaObjectMother
import no.nav.personbruker.minesaker.api.saf.sakstemaer.toInternal
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be instance of`
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test

internal class HentSakstemaerTransformerTest {

    @Test
    fun `Skal kunne transformere fra ekstern til intern modell`() {
        val external = SakstemaObjectMother.giveMeOneSakstema()

        val internal = external.toInternal()

        internal.navn.value `should be equal to` external.navn
        internal.kode.toString() `should be equal to` external.kode.toString()
        internal.sistEndret.shouldNotBeNull()
    }

    @Test
    fun `Skal kaste feil hvis ingen relevante datoer finnes (dette skal ikke kunne skje)`() {
        val external = SakstemaObjectMother.giveMeOneSakstema(journalposter = emptyList())

        val result = runCatching {
            external.toInternal()
        }

        result.isFailure `should be equal to` true
        result.exceptionOrNull() `should be instance of` TransformationException::class
    }

}
