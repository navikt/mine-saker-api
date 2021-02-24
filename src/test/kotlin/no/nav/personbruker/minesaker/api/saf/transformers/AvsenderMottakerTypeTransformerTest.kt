package no.nav.personbruker.minesaker.api.saf.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.saf.domain.AvsenderMottakerType
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

internal class AvsenderMottakerTypeTransformerTest {

    @Test
    fun `Skal transformere alle gyldige verdier, fra ekstern til intern verdi`() {
        AvsenderMottakerTypeTransformer.toInternal(HentJournalposter.AvsenderMottakerIdType.FNR) `should be equal to` AvsenderMottakerType.PERSON
        AvsenderMottakerTypeTransformer.toInternal(HentJournalposter.AvsenderMottakerIdType.HPRNR) `should be equal to` AvsenderMottakerType.HELSEPERSONELL
        AvsenderMottakerTypeTransformer.toInternal(HentJournalposter.AvsenderMottakerIdType.ORGNR) `should be equal to` AvsenderMottakerType.ORGANISASJON
        AvsenderMottakerTypeTransformer.toInternal(HentJournalposter.AvsenderMottakerIdType.UTL_ORG) `should be equal to` AvsenderMottakerType.ORGANISASJON
        AvsenderMottakerTypeTransformer.toInternal(HentJournalposter.AvsenderMottakerIdType.NULL) `should be equal to` AvsenderMottakerType.UKJENT
        AvsenderMottakerTypeTransformer.toInternal(HentJournalposter.AvsenderMottakerIdType.UKJENT) `should be equal to` AvsenderMottakerType.UKJENT
        AvsenderMottakerTypeTransformer.toInternal(HentJournalposter.AvsenderMottakerIdType.__UNKNOWN_VALUE) `should be equal to` AvsenderMottakerType.UKJENT
    }

}