package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.personbruker.minesaker.api.domain.Fodselsnummer
import no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers.AvsenderMottakerObjectMother
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test

internal class DokumentkildeTransformerTest {

    @Test
    fun `Skal transformere fra ekstern til intern verdi, i tilfeller hvor innlogget bruker er avsender`() {
        val expextedIdent = "123"
        val innloggetBruker = Fodselsnummer(expextedIdent)
        val external = AvsenderMottakerObjectMother.giveMePerson(expextedIdent)

        val internal = external.toInternal(innloggetBruker)

        internal.innloggetBrukerErSelvKilden `should be equal to` true
        internal.type.shouldNotBeNull()
    }

    @Test
    fun `Skal transformere fra ekstern til intern verdi, i tilfeller hvor innlogget bruker IKKE er avsender`() {
        val innloggetBruker = Fodselsnummer("456")
        val external = AvsenderMottakerObjectMother.giveMePerson("123")

        val internal = external.toInternal(innloggetBruker)

        internal.innloggetBrukerErSelvKilden `should be equal to` false
        internal.type.shouldNotBeNull()
    }

}
