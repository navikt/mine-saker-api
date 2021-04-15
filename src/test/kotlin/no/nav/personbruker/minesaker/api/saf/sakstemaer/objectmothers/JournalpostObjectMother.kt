package no.nav.personbruker.minesaker.api.saf.sakstemaer.objectmothers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer

object JournalpostObjectMother {

    fun giveMeOneInngaaendeDokument(
        relevanteDatoer: List<HentSakstemaer.RelevantDato?> = listOf(
            RelevantDatoObjectMother.giveMeDatoForInngaaendeDokument(),
            RelevantDatoObjectMother.giveMeDatoForUtgaaendeDokument()
        ),
    ) = HentSakstemaer.Journalpost(
        relevanteDatoer,
    )

}
