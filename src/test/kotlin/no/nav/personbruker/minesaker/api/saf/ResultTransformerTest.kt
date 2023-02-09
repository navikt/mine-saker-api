package no.nav.personbruker.minesaker.api.saf

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.personbruker.minesaker.api.common.exception.TransformationException

import no.nav.personbruker.minesaker.api.saf.journalposter.HentJournalpostResultObjectMother
import no.nav.personbruker.minesaker.api.saf.sakstemaer.HentSakstemaResultObjectMother
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

internal class ResultTransformerTest {

    private val dummyInnloggetBruker = "123"

    @Test
    fun `Skal transformere et SAF-resultat for aa hente inn sakstemaer`() {
        val external = HentSakstemaResultObjectMother.giveMeHentSakstemaResult()

        val internal = external.toInternal()

        internal.shouldNotBeNull()
    }

    @Test
    fun `Skal transformere et SAF-resultat for aa hente inn journalposter`() {
        val external = HentJournalpostResultObjectMother.giveMeHentJournalposterResult()

        val internal = external.toInternal(dummyInnloggetBruker)

        internal.shouldNotBeNull()
    }

    @Test
    fun `Alle feil som skjer skal kastes videre ved henting av sakstemaer`() {
        val eksternalMedValideringsfeil = HentSakstemaResultObjectMother.giveMeHentSakstemaResultMedUfullstendigeData()

        runCatching {
            eksternalMedValideringsfeil.toInternal()

        }.onFailure { exception ->
            exception.shouldBeInstanceOf<TransformationException>()
        }.onSuccess {
            fail("Denne testen skal kaste en feil")
        }
    }

    @Test
    fun `Alle feil som skjer skal kastes videre ved henting av journalposter`() {
        val eksternalMedValideringsfeil = HentJournalpostResultObjectMother.giveMeHentJournalposterResultMedUfullstendigeData()

        runCatching {
            eksternalMedValideringsfeil.toInternal(dummyInnloggetBruker)

        }.onFailure { exception ->
            exception.shouldBeInstanceOf<TransformationException>()

        }.onSuccess {
            fail("Denne testen skal kaste en feil")
        }
    }

}
