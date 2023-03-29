package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import no.nav.personbruker.minesaker.api.saf.journalposter.JournalpostTestData.avsenderMottakerPerson

import org.junit.jupiter.api.Test

internal class DokumentkildeTransformerTest {

    @Test
    fun `Skal transformere fra ekstern til intern verdi, i tilfeller hvor innlogget bruker er avsender`() {
        val expextedIdent = "123"
        val external = avsenderMottakerPerson(expextedIdent)

        val internal = external.toInternal(expextedIdent)

        internal.innloggetBrukerErSelvKilden shouldBe true
        internal.type.shouldNotBeNull()
    }

    @Test
    fun `Skal transformere fra ekstern til intern verdi, i tilfeller hvor innlogget bruker IKKE er avsender`() {
        val innloggetBruker = "456"
        val external = avsenderMottakerPerson("123")

        val internal = external.toInternal(innloggetBruker)

        internal.innloggetBrukerErSelvKilden shouldBe false
        internal.type.shouldNotBeNull()
    }

}
