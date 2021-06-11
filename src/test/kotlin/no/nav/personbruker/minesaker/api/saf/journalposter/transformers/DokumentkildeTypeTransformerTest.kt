package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.domain.DokumentkildeType
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

internal class DokumentkildeTypeTransformerTest {

    @Test
    fun `Skal transformere alle gyldige verdier, fra ekstern til intern verdi`() {
        HentJournalposter.AvsenderMottakerIdType.FNR.toInternal() `should be equal to` DokumentkildeType.PERSON
        HentJournalposter.AvsenderMottakerIdType.HPRNR.toInternal() `should be equal to` DokumentkildeType.HELSEPERSONELL
        HentJournalposter.AvsenderMottakerIdType.ORGNR.toInternal() `should be equal to` DokumentkildeType.ORGANISASJON
        HentJournalposter.AvsenderMottakerIdType.UTL_ORG.toInternal() `should be equal to` DokumentkildeType.ORGANISASJON
        HentJournalposter.AvsenderMottakerIdType.NULL.toInternal() `should be equal to` DokumentkildeType.UKJENT
        HentJournalposter.AvsenderMottakerIdType.UKJENT.toInternal() `should be equal to` DokumentkildeType.UKJENT
        HentJournalposter.AvsenderMottakerIdType.__UNKNOWN_VALUE.toInternal() `should be equal to` DokumentkildeType.UKJENT
    }

}
