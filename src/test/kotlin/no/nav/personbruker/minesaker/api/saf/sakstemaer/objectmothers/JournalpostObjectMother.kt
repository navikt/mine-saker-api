package no.nav.personbruker.minesaker.api.saf.sakstemaer.objectmothers

import no.nav.personbruker.minesaker.api.saf.sakstemaer.transformers.GraphQLJournalpost
import no.nav.personbruker.minesaker.api.saf.sakstemaer.transformers.GraphQLRelevantDato

object JournalpostObjectMother {

    fun giveMeOneInngaaendeDokument(
        relevanteDatoer: List<GraphQLRelevantDato?> = listOf(
            RelevantDatoObjectMother.giveMeDatoForInngaaendeDokument(),
            RelevantDatoObjectMother.giveMeDatoForUtgaaendeDokument()
        ),
    ) = GraphQLJournalpost(
        relevanteDatoer
    )

}
