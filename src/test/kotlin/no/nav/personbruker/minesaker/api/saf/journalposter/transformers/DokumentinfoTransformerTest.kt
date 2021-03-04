package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.personbruker.minesaker.api.common.exception.MissingFieldException
import no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers.DokumentInfoObjectMother
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be instance of`
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Test

internal class DokumentinfoTransformerTest {

    @Test
    fun `Skal returnere tom liste hvis det ikke finnes dokumenter med arkiverte varianter`() {
        val externals = listOf(DokumentInfoObjectMother.giveMeDokumentUtenArkivertVariant())

        val internals = DokumentInfoTransformer.toInternal(externals)

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

        val internals = DokumentInfoTransformer.toInternal(externals)

        internals.shouldNotBeEmpty()
        internals.size `should be equal to` 1
        internals[0].tittel.value `should be equal to` expectedDokument.tittel
        internals[0].filuuid.value `should be equal to` expectedDokumentVariant?.filuuid
        internals[0].brukerHarTilgang `should be equal to` expectedDokumentVariant?.brukerHarTilgang
    }

    @Test
    fun `Skal kaste feil hvis dokumentlisten er null`() {
        val result = runCatching {
            DokumentInfoTransformer.toInternal(null)
        }
        result.isFailure `should be equal to` true
        result.exceptionOrNull() `should be instance of` MissingFieldException::class
        val mfe = result.exceptionOrNull() as MissingFieldException
        mfe.context["feltnavn"] `should be equal to` "dokumenter"
    }

    @Test
    fun `Skal kaste feil hvis tittel-feltet mangler`() {
        val externals = listOf(DokumentInfoObjectMother.giveMeDokumentMedArkivertVariantMenUtenTittel())

        val result = runCatching {
            DokumentInfoTransformer.toInternal(externals)
        }
        result.isFailure `should be equal to` true
        result.exceptionOrNull() `should be instance of` MissingFieldException::class
        val mfe = result.exceptionOrNull() as MissingFieldException
        mfe.context["feltnavn"] `should be equal to` "tittel"
    }

    @Test
    fun `Skal kaste feil hvis filuuid-feltet mangler`() {
        val externals = listOf(DokumentInfoObjectMother.giveMeDokumentMedArkivertVariantMenUtenFiluuid())

        val result = runCatching {
            DokumentInfoTransformer.toInternal(externals)
        }
        result.isFailure `should be equal to` true
        result.exceptionOrNull() `should be instance of` MissingFieldException::class
        val mfe = result.exceptionOrNull() as MissingFieldException
        mfe.context["feltnavn"] `should be equal to` "filuuid"
    }

    @Test
    fun `Skal sette tilgang til dokumentet som false hvis det ikke er spesifisert`() {
        val externals = listOf(DokumentInfoObjectMother.giveMeDokumentMedArkivertVariantMenUtenAtTilgangErSpesifisert())

        val internals = DokumentInfoTransformer.toInternal(externals)

        internals.shouldNotBeEmpty()
        internals.size `should be equal to` 1
        internals[0].brukerHarTilgang `should be equal to` false
    }

}
