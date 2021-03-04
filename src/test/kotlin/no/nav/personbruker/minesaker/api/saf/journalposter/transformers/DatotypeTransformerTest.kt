package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.saf.domain.Datotype
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

internal class DatotypeTransformerTest {

    @Test
    fun `Skal transformere alle gyldige verdier, fra ekstern til intern verdi`() {
        Datotype.values().size `should be equal to` HentJournalposter.Datotype.values().size

        HentJournalposter.Datotype.DATO_SENDT_PRINT.toInternal() `should be equal to` Datotype.SENDT_PRINT
        HentJournalposter.Datotype.DATO_JOURNALFOERT.toInternal() `should be equal to` Datotype.JOURNALFORT
        HentJournalposter.Datotype.DATO_DOKUMENT.toInternal() `should be equal to` Datotype.DOKUMENT
        HentJournalposter.Datotype.DATO_AVS_RETUR.toInternal() `should be equal to` Datotype.AV_RETUR
        HentJournalposter.Datotype.DATO_EKSPEDERT.toInternal() `should be equal to` Datotype.EKSPEDERT
        HentJournalposter.Datotype.DATO_OPPRETTET.toInternal() `should be equal to` Datotype.OPPRETTET
        HentJournalposter.Datotype.DATO_REGISTRERT.toInternal() `should be equal to` Datotype.REGISTRERT
        HentJournalposter.Datotype.__UNKNOWN_VALUE.toInternal() `should be equal to` Datotype.UKJENT
    }

}
