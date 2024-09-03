package no.nav.tms.minesaker.api.saf.journalposter.transformers

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.tms.minesaker.api.saf.sakstemaer.SakstemaException
import no.nav.tms.minesaker.api.saf.journalposter.v1.Dokumentvariant
import no.nav.tms.minesaker.api.saf.journalposter.v1.SafVariantformat
import no.nav.tms.minesaker.api.saf.journalposter.v1.toInternal
import org.junit.jupiter.api.Test

internal class DokumentvariantTransformerTest {

    @Test
    fun `Skal kunne konvertere de to variantformatene som brukes`() {
        SafVariantformat.ARKIV.toInternal() shouldBe Dokumentvariant.ARKIV
        SafVariantformat.SLADDET.toInternal() shouldBe Dokumentvariant.SLADDET
    }

    @Test
    fun `Skal kaste feil hvis det blir forsokt aa transformere et variantformat som ikke brukes`() {
        val result = runCatching {
            SafVariantformat.__UNKNOWN_VALUE.toInternal()
        }

        result.isFailure shouldBe true
        val exception = result.exceptionOrNull()
        exception.shouldBeInstanceOf<SakstemaException>()
        exception.type shouldBe SakstemaException.ErrorType.INVALID_STATE
    }

}
