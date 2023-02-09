package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import io.kotest.matchers.shouldBe
import no.nav.personbruker.minesaker.api.domain.DokumentkildeType
import org.junit.jupiter.api.Test

internal class DokumentkildeTypeTransformerTest {

    @Test
    fun `Skal transformere alle gyldige verdier, fra ekstern til intern verdi`() {
        GraphQLAvsenderMottakerIdType.FNR.toInternal() shouldBe DokumentkildeType.PERSON
        GraphQLAvsenderMottakerIdType.HPRNR.toInternal() shouldBe DokumentkildeType.HELSEPERSONELL
        GraphQLAvsenderMottakerIdType.ORGNR.toInternal() shouldBe DokumentkildeType.ORGANISASJON
        GraphQLAvsenderMottakerIdType.UTL_ORG.toInternal() shouldBe DokumentkildeType.ORGANISASJON
        GraphQLAvsenderMottakerIdType.NULL.toInternal() shouldBe DokumentkildeType.UKJENT
        GraphQLAvsenderMottakerIdType.UKJENT.toInternal() shouldBe DokumentkildeType.UKJENT
        GraphQLAvsenderMottakerIdType.__UNKNOWN_VALUE.toInternal() shouldBe DokumentkildeType.UKJENT
    }

}
