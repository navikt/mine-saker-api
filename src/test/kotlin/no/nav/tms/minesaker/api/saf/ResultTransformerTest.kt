package no.nav.tms.minesaker.api.saf

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.tms.minesaker.api.saf.sakstemaer.SakstemaException
import no.nav.tms.minesaker.api.saf.journalposter.JournalpostTestData.listOfSakstemaer
import no.nav.tms.minesaker.api.saf.journalposter.JournalpostTestData.sakstemaWithUtgaaendeDokument
import no.nav.tms.minesaker.api.saf.journalposter.v1.SafDokumentoversikt
import no.nav.tms.minesaker.api.saf.journalposter.v1.toInternal
import no.nav.tms.minesaker.api.saf.sakstemaer.HentSakstemaResultTestData
import no.nav.tms.minesaker.api.saf.sakstemaer.toInternal
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

internal class ResultTransformerTest {

    private val dummyInnloggetBruker = "123"
    private val dummyResolver = InnsynsUrlResolver(mapOf(), "http://dummy.innsyn.no")

    @Test
    fun `Skal transformere et SAF-resultat for aa hente inn sakstemaer`() {
        val external = HentSakstemaResultTestData.result()

        val internal = external.toInternal(dummyResolver)

        internal.shouldNotBeNull()
    }

    @Test
    fun `Skal transformere et SAF-resultat for aa hente inn journalposter`() {
        val temaer = listOfSakstemaer()
        val external = HentJournalposter.Result(SafDokumentoversikt(temaer))

        val internal = external.toInternal(dummyInnloggetBruker)

        internal.shouldNotBeNull()
    }

    @Test
    fun `Alle feil som skjer skal kastes videre ved henting av sakstemaer`() {
        val eksternalMedValideringsfeil = HentSakstemaResultTestData.medUfullstendigeData()

        runCatching {
            eksternalMedValideringsfeil.toInternal(dummyResolver)

        }.onFailure { exception ->
            exception.shouldBeInstanceOf<SakstemaException>()
        }.onSuccess {
            fail("Denne testen skal kaste en feil")
        }
    }

    @Test
    fun `Alle feil som skjer skal kastes videre ved henting av journalposter`() {
        val sakstemaUtenKodeSatt = sakstemaWithUtgaaendeDokument(kode = "UGYLDIG_VERDI")

        val eksternalMedValideringsfeil =
            HentJournalposter.Result(SafDokumentoversikt(listOf(sakstemaUtenKodeSatt)))

        runCatching {
            eksternalMedValideringsfeil.toInternal(dummyInnloggetBruker)

        }.onFailure { exception ->
            exception.shouldBeInstanceOf<SakstemaException>()

        }.onSuccess {
            fail("Denne testen skal kaste en feil")
        }
    }

}
