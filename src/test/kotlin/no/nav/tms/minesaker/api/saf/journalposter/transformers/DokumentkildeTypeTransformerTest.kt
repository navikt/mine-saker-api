package no.nav.tms.minesaker.api.saf.journalposter.transformers

import io.kotest.matchers.shouldBe
import no.nav.tms.minesaker.api.saf.journalposter.v1.DokumentkildeType
import no.nav.tms.minesaker.api.saf.journalposter.v1.SafAvsenderMottakerIdType
import no.nav.tms.minesaker.api.saf.journalposter.v1.toInternal
import org.junit.jupiter.api.Test

internal class DokumentkildeTypeTransformerTest {

    @Test
    fun `Skal transformere alle gyldige verdier, fra ekstern til intern verdi`() {
        SafAvsenderMottakerIdType.FNR.toInternal() shouldBe DokumentkildeType.PERSON
        SafAvsenderMottakerIdType.HPRNR.toInternal() shouldBe DokumentkildeType.HELSEPERSONELL
        SafAvsenderMottakerIdType.ORGNR.toInternal() shouldBe DokumentkildeType.ORGANISASJON
        SafAvsenderMottakerIdType.UTL_ORG.toInternal() shouldBe DokumentkildeType.ORGANISASJON
        SafAvsenderMottakerIdType.NULL.toInternal() shouldBe DokumentkildeType.UKJENT
        SafAvsenderMottakerIdType.UKJENT.toInternal() shouldBe DokumentkildeType.UKJENT
        SafAvsenderMottakerIdType.__UNKNOWN_VALUE.toInternal() shouldBe DokumentkildeType.UKJENT
    }

}
