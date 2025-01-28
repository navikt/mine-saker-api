package no.nav.tms.minesaker.api.saf

import io.kotest.matchers.nulls.shouldNotBeNull
import no.nav.dokument.saf.selvbetjening.generated.dto.AlleJournalposter
import no.nav.dokument.saf.selvbetjening.generated.dto.allejournalposter.Dokumentoversikt
import no.nav.tms.minesaker.api.saf.journalposter.JournalpostTestData.listOfSakstemaer
import no.nav.tms.minesaker.api.saf.journalposter.toInternal
import org.junit.jupiter.api.Test

internal class ResultTransformerTest {

    private val dummyInnloggetBruker = "123"

    @Test
    fun `Skal transformere et SAF-resultat for å hente inn journalposter`() {
        val temaer = listOfSakstemaer()
        val external = AlleJournalposter.Result(Dokumentoversikt(temaer))

        val internal = external.toInternal()

        internal.shouldNotBeNull()
    }
}
