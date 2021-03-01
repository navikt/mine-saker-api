package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.saf.domain.Datotype
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

internal class DatotypeTransformerTest {

    @Test
    fun `Skal transformere alle gyldige verdier, fra ekstern til intern verdi`() {
        Datotype.values().size `should be equal to` HentJournalposter.Datotype.values().size

        DatotypeTransformer.toInternal(HentJournalposter.Datotype.DATO_SENDT_PRINT) `should be equal to` Datotype.SENDT_PRINT
        DatotypeTransformer.toInternal(HentJournalposter.Datotype.DATO_JOURNALFOERT) `should be equal to` Datotype.JOURNALFORT
        DatotypeTransformer.toInternal(HentJournalposter.Datotype.DATO_DOKUMENT) `should be equal to` Datotype.DOKUMENT
        DatotypeTransformer.toInternal(HentJournalposter.Datotype.DATO_AVS_RETUR) `should be equal to` Datotype.AV_RETUR
        DatotypeTransformer.toInternal(HentJournalposter.Datotype.DATO_EKSPEDERT) `should be equal to` Datotype.EKSPEDERT
        DatotypeTransformer.toInternal(HentJournalposter.Datotype.DATO_OPPRETTET) `should be equal to` Datotype.OPPRETTET
        DatotypeTransformer.toInternal(HentJournalposter.Datotype.DATO_REGISTRERT) `should be equal to` Datotype.REGISTRERT
        DatotypeTransformer.toInternal(HentJournalposter.Datotype.__UNKNOWN_VALUE) `should be equal to` Datotype.UKJENT
    }

}
