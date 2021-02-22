package no.nav.personbruker.minesaker.api.saf


import no.nav.dokument.saf.selvbetjening.generated.dto.HentKonkretSakstemaDTO
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test

internal class HentJournalposterDtoTransformerTest {

    @Test
    fun `Skal kunne transformere fra ekstern til intern modell - Hent konkret sakstema`() {
        val external = HentKonkretSakstemaDTO.Sakstema("navn", "kode")

        val internal = HentKonkretSakstemaDtoTransformer.toInternal(external)

        internal.navn `should be equal to` external.navn
        internal.kode `should be equal to` external.kode
    }

    @Test
    fun `Skal kunne transformere fra flere eksterne til interne - Hent konkret sakstema`() {
        val externals = listOf(
            HentKonkretSakstemaDTO.Sakstema("navn1", "kode1"),
            HentKonkretSakstemaDTO.Sakstema("navn2", "kode2")
        )

        val internals = HentKonkretSakstemaDtoTransformer.toInternal(externals)

        internals.size `should be equal to` externals.size
        internals.forEach { internal ->
            internal.shouldNotBeNull()
        }
    }

}
