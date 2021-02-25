package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.personbruker.minesaker.api.common.exception.MissingFieldException
import no.nav.personbruker.minesaker.api.saf.domain.Journalposttype
import no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers.RelevantDatoObjectMother
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be instance of`
import org.amshove.kluent.`should not be null`
import org.junit.jupiter.api.Test

internal class RelevantDatoTransformerTest {

    @Test
    fun `Skal konvertere dato som string til dato type med tidssone satt`() {
        val external = RelevantDatoObjectMother.giveMeDatoForUtgaaendeDokument()

        val internal = RelevantDatoTransformer.toInternal(external.dato)

        internal.`should not be null`()
        internal.year `should be equal to` 2018
        internal.monthValue `should be equal to` 1
        internal.monthValue `should be equal to` 1
        internal.hour `should be equal to` 12
        internal.minute `should be equal to` 0
        internal.second `should be equal to` 0
    }

    @Test
    fun `Skal plukke ut riktig dato for inngaaende dokumenter`() {
        val externals = RelevantDatoObjectMother.giveMeOneOfEachEkspederRegistertAndOpprettet()
        val expectedDato =
            RelevantDatoTransformer.toInternal(RelevantDatoObjectMother.giveMeDatoForInngaaendeDokument().dato)

        val internalDato = RelevantDatoTransformer.toInternal(externals, Journalposttype.INNGAAENDE)

        internalDato `should be equal to` expectedDato
    }

    @Test
    fun `Skal plukke ut riktig dato for utgaaende dokumenter`() {
        val externals = RelevantDatoObjectMother.giveMeOneOfEachEkspederRegistertAndOpprettet()
        val expectedDato =
            RelevantDatoTransformer.toInternal(RelevantDatoObjectMother.giveMeDatoForUtgaaendeDokument().dato)

        val internalDato = RelevantDatoTransformer.toInternal(externals, Journalposttype.UTGAAENDE)

        internalDato `should be equal to` expectedDato
    }

    @Test
    fun `Skal plukke ut riktig dato for notater`() {
        val externals = RelevantDatoObjectMother.giveMeOneOfEachEkspederRegistertAndOpprettet()
        val expectedDato = RelevantDatoTransformer.toInternal(RelevantDatoObjectMother.giveMeDatoForNotat().dato)

        val internalDato = RelevantDatoTransformer.toInternal(externals, Journalposttype.NOTAT)

        internalDato `should be equal to` expectedDato
    }

    @Test
    fun `Skal kaste feil hvis ikke riktig dato for dokumenttypen finnes`() {
        val externals = listOf(
            RelevantDatoObjectMother.giveMeDatoForNotat(),
            RelevantDatoObjectMother.giveMeDatoForUtgaaendeDokument()
        )

        val result = runCatching {
            RelevantDatoTransformer.toInternal(externals, Journalposttype.INNGAAENDE)
        }

        result.isFailure `should be equal to` true
        result.exceptionOrNull() `should be instance of` MissingFieldException::class
        val exception = result.exceptionOrNull() as MissingFieldException
        exception.context["feltnavn"] `should be equal to` "dato_registrert"
    }

}
