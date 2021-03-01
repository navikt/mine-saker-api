package no.nav.personbruker.minesaker.api.saf

import org.amshove.kluent.`should not be null`
import org.junit.jupiter.api.Test

internal class ResultTransformerTest {

    @Test
    fun `Skal transformere et SAF-resultat for aa hente inn sakstemaer`() {
        val hentSakstemaer = ResultObjectMother.giveMeHentSakstemaResult()

        val internal = ResultTransformer.toInternal(hentSakstemaer)

        internal.`should not be null`()
    }

    @Test
    fun `Skal transformere et SAF-resultat for aa hente inn journalposter`() {
        val hentJournalposter = ResultObjectMother.giveMeHentJournalposterResult()

        val internal = ResultTransformer.toInternal(hentJournalposter)

        internal.`should not be null`()
    }

}
