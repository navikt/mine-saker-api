package no.nav.personbruker.minesaker.api.saf

import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakerDTO
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test

internal class HentSakerDtoTransformerTest {

    @Test
    fun `Skal kunne transformere fra ekstern til intern modell - Hent saker`() {
        val external = HentSakerDTO.Sakstema("navn", "kode")

        val internal = HentSakerDtoTransformer.toInternal(external)

        internal.navn `should be equal to` external.navn
        internal.kode `should be equal to` external.kode
    }

    @Test
    fun `Skal kunne transformere fra flere eksterne til interne - Hent saker`() {
        val externals = listOf(
            HentSakerDTO.Sakstema("navn1", "kode1"),
            HentSakerDTO.Sakstema("navn2", "kode2")
        )

        val internals = HentSakerDtoTransformer.toInternal(externals)

        internals.size `should be equal to` externals.size
        internals.forEach { internal ->
            internal.shouldNotBeNull()
        }
    }

}
