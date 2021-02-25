package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.personbruker.minesaker.api.common.exception.MissingFieldException
import no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers.AvsenderMottakerObjectMother
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be instance of`
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test

internal class AvsenderMottakerTransformerTest {

    @Test
    fun `Skal transformere alle gyldige verdier, fra ekstern til intern verdi`() {
        val external = AvsenderMottakerObjectMother.giveMePersonSomAvsender("123")

        val internal = AvsenderMottakerTransformer.toInternal(external)

        internal.id `should be equal to` external.id
        internal.type.shouldNotBeNull()
    }

    @Test
    fun `Skal kaste feil hvis input er null`() {
        val result = runCatching {
            AvsenderMottakerTransformer.toInternal(null)
        }
        result.isFailure `should be equal to` true
        result.exceptionOrNull() `should be instance of` MissingFieldException::class
        val mfe = result.exceptionOrNull() as MissingFieldException
        mfe.context["feltnavn"] `should be equal to` "avsenderMottaker"
    }

}
