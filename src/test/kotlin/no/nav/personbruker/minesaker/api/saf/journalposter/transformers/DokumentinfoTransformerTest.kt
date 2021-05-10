package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.personbruker.minesaker.api.common.exception.TransformationException
import no.nav.personbruker.minesaker.api.saf.domain.Dokumenttype
import no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers.DokumentInfoObjectMother
import org.amshove.kluent.*
import org.junit.jupiter.api.Test

internal class DokumentinfoTransformerTest {

    @Test
    fun `Skal markere forste dokument som hoveddokument, og resten som vedlegg`() {
        val externals = DokumentInfoObjectMother.giveMeTreGyldigeDokumenter()

        val internals = externals.toInternal()

        internals.shouldNotBeNull()
        internals.size `should be equal to` externals.size
        internals[0].dokumenttype `should be equal to` Dokumenttype.HOVED
        internals[0].tittel.value `should be equal to` externals[0].tittel
        internals[1].dokumenttype `should be equal to` Dokumenttype.VEDLEGG
        internals[1].tittel.value `should be equal to` externals[1].tittel
        internals[2].dokumenttype `should be equal to` Dokumenttype.VEDLEGG
        internals[2].tittel.value `should be equal to` externals[2].tittel
    }

    @Test
    fun `Kaste feil hvis dokument ikke har varianter`() {
        val externals = listOf(DokumentInfoObjectMother.giveMeDokumentUtenNoenVarianter())

        val result = runCatching {
            externals.toInternal()
        }

        result.isFailure `should be equal to` true
        result.exceptionOrNull() `should be instance of` TransformationException::class
        val exception = result.exceptionOrNull() as TransformationException
        exception.context["feltnavn"] `should be equal to` "dokumentvarianter"
    }

    @Test
    fun `Skal takle at et dokument har flere varianter, og plukke den forste varianten`() {
        val externals = listOf(DokumentInfoObjectMother.giveMeDokumentMedFlereVarianter())

        val internals = externals.toInternal()

        internals.shouldNotBeNull()
        internals.size `should be equal to` 1
    }

    @Test
    fun `Skal takle at tittel ikke er tilgjengelig i SAF, return dummy tittel til sluttbruker`() {
        val externals = listOf(DokumentInfoObjectMother.giveMeDokumentMedArkivertVariantMenUtenTittel())

        val result = externals.toInternal()

        result[0].tittel.value `should be equal to` "Uten tittel"
    }

    @Test
    fun `Skal sette tilgang til dokumentet som false hvis det ikke er spesifisert`() {
        val externals = listOf(DokumentInfoObjectMother.giveMeDokumentMedArkivertVariantMenUtenAtTilgangErSpesifisert())
        val expectedGrunnTilManglendeTilgang = externals[0].dokumentvarianter[0]?.code?.get(0)

        val internals = externals.toInternal()

        internals.shouldNotBeEmpty()
        internals.size `should be equal to` 1
        internals[0].brukerHarTilgang `should be equal to` false
        internals[0].eventuelleGrunnerTilManglendeTilgang.size `should be equal to` 1
        internals[0].eventuelleGrunnerTilManglendeTilgang[0] `should be equal to` expectedGrunnTilManglendeTilgang
    }

}
