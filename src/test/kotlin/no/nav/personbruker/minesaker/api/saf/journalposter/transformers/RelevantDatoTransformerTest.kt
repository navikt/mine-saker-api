package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers.RelevantDatoObjectMother
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test

internal class RelevantDatoTransformerTest {

    @Test
    fun `Skal transformere til intern modell, og plukke ut den datoen som er nyest`() {
        val datoer = RelevantDatoObjectMother.giveMeOneOfEachEkspederRegistertAndOpprettet()

        val nyesteDato = datoer.toInternal()

        nyesteDato `should be equal to` RelevantDatoObjectMother.giveMeDatoForNotat().toInternal()
    }

    @Test
    fun `Skal haandtere at to datoer er like`() {
        val expectedDato = RelevantDatoObjectMother.giveMeDatoForInngaaendeDokument()
        val toLikeDatoer = listOf(expectedDato, expectedDato)

        val nyesteDato = toLikeDatoer.toInternal()

        nyesteDato `should be equal to` expectedDato.toInternal()
    }

    @Test
    fun `Skal haandtere at en relevantDato er null`() {
        val enDatoErNull = mutableListOf<HentJournalposter.RelevantDato?>(null)
        enDatoErNull.addAll( RelevantDatoObjectMother.giveMeOneOfEachEkspederRegistertAndOpprettet())

        val nyesteDato = enDatoErNull.toInternal()

        nyesteDato.shouldNotBeNull()
    }

}
