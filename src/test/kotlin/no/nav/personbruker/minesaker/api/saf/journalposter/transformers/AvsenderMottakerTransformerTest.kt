package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers.AvsenderMottakerObjectMother
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test

internal class AvsenderMottakerTransformerTest {

    @Test
    fun `Skal transformere alle gyldige verdier, fra ekstern til intern verdi`() {
        val external = AvsenderMottakerObjectMother.giveMePersonSomAvsender("123")

        val internal = external.toInternal()

        internal.id `should be equal to` external.id
        internal.type.shouldNotBeNull()
    }

}
