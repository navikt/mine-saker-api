package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers.DokumentInfoObjectMother
import org.amshove.kluent.*
import org.junit.jupiter.api.Test

internal class DokumentinfoTransformerTest {

    @Test
    fun `Skal returnere tom liste hvis det ikke finnes dokumenter med arkiverte varianter`() {
        val externals = listOf(DokumentInfoObjectMother.giveMeDokumentUtenArkivertVariant())

        val internals = externals.toInternal()

        internals.shouldBeEmpty()
    }

    @Test
    fun `Skal returnere kun dokumenter som har en arkivert variant`() {
        val expectedDokument = DokumentInfoObjectMother.giveMeDokumentMedArkivertVariant()
        val expectedDokumentVariant = expectedDokument.getEventuellArkivertVariant()
        val externals = listOf(
            DokumentInfoObjectMother.giveMeDokumentUtenArkivertVariant(),
            expectedDokument
        )

        val internals = externals.toInternal()

        internals.shouldNotBeEmpty()
        internals.size `should be equal to` 1
        internals[0].tittel.value `should be equal to` expectedDokument.tittel
        internals[0].dokumentInfoId.value `should be equal to` expectedDokument.dokumentInfoId
        internals[0].brukerHarTilgang `should be equal to` expectedDokumentVariant?.brukerHarTilgang
        internals[0].eventuelleGrunnerTilManglendeTilgang.`should be empty`()
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
