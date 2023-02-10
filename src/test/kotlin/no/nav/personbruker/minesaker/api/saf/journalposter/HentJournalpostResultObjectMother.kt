package no.nav.personbruker.minesaker.api.saf.journalposter

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers.SakstemaObjectMother
import no.nav.personbruker.minesaker.api.saf.journalposter.transformers.GraphQLDokumentoversikt

object HentJournalpostResultObjectMother {
    fun giveMeHentJournalposterResult(): HentJournalposter.Result {
        val temaer = SakstemaObjectMother.giveMeListOfSakstemaer()
        val dokumentoversikt = GraphQLDokumentoversikt(temaer)
        return HentJournalposter.Result(dokumentoversikt)
    }

    fun giveMeHentJournalposterResultMedUfullstendigeData(): HentJournalposter.Result {
        val sakstemaUtenKodeSatt = SakstemaObjectMother.giveMeSakstemaWithUtgaaendeDokument(kode = "UGYLDIG_VERDI")
        val dokumentoversikt = GraphQLDokumentoversikt(listOf(sakstemaUtenKodeSatt))
        return HentJournalposter.Result(dokumentoversikt)
    }
}
