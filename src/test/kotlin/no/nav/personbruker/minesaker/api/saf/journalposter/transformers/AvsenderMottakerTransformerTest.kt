package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.personbruker.minesaker.api.common.exception.TransformationException
import no.nav.personbruker.minesaker.api.domain.Fodselsnummer
import no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers.AvsenderMottakerObjectMother
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be instance of`
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test

internal class AvsenderMottakerTransformerTest {

    private val dummyIdent = Fodselsnummer("123")

    @Test
    fun `Skal transformere fra ekstern til intern verdi, i tilfeller hvor innlogget bruker er avsender`() {
        val expextedIdent = "123"
        val innloggetBruker = Fodselsnummer(expextedIdent)
        val external = AvsenderMottakerObjectMother.giveMePerson(expextedIdent)

        val internal = external.toInternal(innloggetBruker)

        internal.innloggetBrukerErAvsender `should be equal to` true
        internal.type.shouldNotBeNull()
    }

    @Test
    fun `Skal transformere fra ekstern til intern verdi, i tilfeller hvor innlogget bruker IKKE er avsender`() {
        val innloggetBruker = Fodselsnummer("456")
        val external = AvsenderMottakerObjectMother.giveMePerson("123")

        val internal = external.toInternal(innloggetBruker)

        internal.innloggetBrukerErAvsender `should be equal to` false
        internal.type.shouldNotBeNull()
    }

    @Test
    fun `Skal kaste feil hvis idType ikke er satt`() {
        val externalUtenTypeSatt = AvsenderMottakerObjectMother.giveMePerson(idType = null)

        val result = runCatching {
            externalUtenTypeSatt.toInternal(dummyIdent)
        }

        result.isFailure `should be equal to` true
        result.exceptionOrNull() `should be instance of` TransformationException::class
        val exception = result.exceptionOrNull() as TransformationException
        exception.context[TransformationException.feltnavnKey] `should be equal to` "avsenderMottakerIdType"
    }

    @Test
    fun `Hvis ID ikke er satt, saa skal innloggetBrukerErAvsender settes til false`() {
        val externalUtenTypeSatt = AvsenderMottakerObjectMother.giveMePerson(ident = null)

        val internal = externalUtenTypeSatt.toInternal(dummyIdent)

        internal.innloggetBrukerErAvsender `should be equal to` false
        internal.type.shouldNotBeNull()
    }

}
