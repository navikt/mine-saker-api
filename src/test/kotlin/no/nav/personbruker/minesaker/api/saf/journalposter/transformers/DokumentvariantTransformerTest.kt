package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.common.exception.TransformationException
import no.nav.personbruker.minesaker.api.domain.Dokumentvariant
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be instance of`
import org.junit.jupiter.api.Test

internal class DokumentvariantTransformerTest {

    @Test
    fun `Skal kunne konvertere de to variantformatene som brukes`() {
        HentJournalposter.Variantformat.ARKIV.toInternal() `should be equal to` Dokumentvariant.ARKIV
        HentJournalposter.Variantformat.SLADDET.toInternal() `should be equal to` Dokumentvariant.SLADDET
    }

    @Test
    fun `Skal kaste feil hvis det blir forsokt aa transformere et variantformat som ikke brukes`() {
        val result = runCatching {
            HentJournalposter.Variantformat.__UNKNOWN_VALUE.toInternal()
        }

        result.isFailure `should be equal to` true
        val exception = result.exceptionOrNull()
        exception `should be instance of` TransformationException::class
        exception as TransformationException
        exception.type `should be equal to` TransformationException.ErrorType.INVALID_STATE
    }

}
