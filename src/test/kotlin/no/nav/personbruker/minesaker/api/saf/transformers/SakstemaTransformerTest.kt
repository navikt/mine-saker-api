package no.nav.personbruker.minesaker.api.saf.transformers


import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.saf.domain.JournalpostObjectMother
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should not be empty`
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test

internal class SakstemaTransformerTest {

    @Test
    fun `Skal kunne transformere fra ekstern til intern modell - Hent konkret sakstema`() {
        val journalposter = listOf(JournalpostObjectMother.giveMeOneInngaaendeDokument())
        val external = HentJournalposter.Sakstema("navn", "kode", journalposter)

        val internal = SakstemaTransformer.toInternal(external)

        internal.navn `should be equal to` external.navn
        internal.kode `should be equal to` external.kode
        internal.journalposter.`should not be empty`()
        internal.journalposter.size `should be equal to` 1
    }

    @Test
    fun `Skal kunne transformere fra flere eksterne til interne - Hent konkret sakstema`() {
        val externals = listOf(
            HentJournalposter.Sakstema("navn1", "kode1", emptyList()),
            HentJournalposter.Sakstema("navn2", "kode2", emptyList())
        )

        val internals = SakstemaTransformer.toInternal(externals)

        internals.size `should be equal to` externals.size
        internals.forEach { internal ->
            internal.shouldNotBeNull()
        }
    }

}
