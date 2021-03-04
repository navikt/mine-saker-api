package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.saf.domain.AvsenderMottakerType
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

internal class AvsenderMottakerTypeTransformerTest {

    @Test
    fun `Skal transformere alle gyldige verdier, fra ekstern til intern verdi`() {
        HentJournalposter.AvsenderMottakerIdType.FNR.toInternal() `should be equal to` AvsenderMottakerType.PERSON
        HentJournalposter.AvsenderMottakerIdType.HPRNR.toInternal() `should be equal to` AvsenderMottakerType.HELSEPERSONELL
        HentJournalposter.AvsenderMottakerIdType.ORGNR.toInternal() `should be equal to` AvsenderMottakerType.ORGANISASJON
        HentJournalposter.AvsenderMottakerIdType.UTL_ORG.toInternal() `should be equal to` AvsenderMottakerType.ORGANISASJON
        HentJournalposter.AvsenderMottakerIdType.NULL.toInternal() `should be equal to` AvsenderMottakerType.UKJENT
        HentJournalposter.AvsenderMottakerIdType.UKJENT.toInternal() `should be equal to` AvsenderMottakerType.UKJENT
        HentJournalposter.AvsenderMottakerIdType.__UNKNOWN_VALUE.toInternal() `should be equal to` AvsenderMottakerType.UKJENT
    }

}
