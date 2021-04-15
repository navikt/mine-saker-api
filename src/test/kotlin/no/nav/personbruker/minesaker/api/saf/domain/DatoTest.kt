package no.nav.personbruker.minesaker.api.saf.domain

import no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers.RelevantDatoObjectMother
import no.nav.personbruker.minesaker.api.saf.journalposter.transformers.toInternal
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

internal class DatoTest {

    @Test
    fun `Skal plukke ut den datoen som er nyest`() {
        val datoer = RelevantDatoObjectMother.giveMeOneOfEachEkspederRegistertAndOpprettet().map { it.toInternal() }

        val nyesteDato = datoer.plukkUtNyesteDato()

        nyesteDato `should be equal to` RelevantDatoObjectMother.giveMeDatoForNotat().toInternal()
    }

    @Test
    fun `Skal haandtere at to datoer er like`() {
        val expectedDato = RelevantDatoObjectMother.giveMeDatoForInngaaendeDokument().toInternal()
        val toLikeDatoer = listOf(expectedDato, expectedDato)

        val nyesteDato = toLikeDatoer.plukkUtNyesteDato()

        nyesteDato `should be equal to` expectedDato
    }

}
