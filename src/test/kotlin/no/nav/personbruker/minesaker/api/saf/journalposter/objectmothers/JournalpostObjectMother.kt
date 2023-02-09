package no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers

import no.nav.personbruker.minesaker.api.saf.journalposter.transformers.*

object JournalpostObjectMother {

    fun giveMeOneInngaaendeDokument(
        tittel: String? = "Dummytittel Inngående",
        journalpostId: String = "dummyId-Inngående",
        journalposttype: GraphQLJournalposttype = GraphQLJournalposttype.I,
        avsender: GraphQLAvsenderMottaker? = AvsenderMottakerObjectMother.giveMePerson("123"),
        mottaker: GraphQLAvsenderMottaker? = AvsenderMottakerObjectMother.giveMeOrganisasjon("654"),
        relevanteDatoer: List<GraphQLRelevantDato?> = listOf(
            RelevantDatoObjectMother.giveMeDatoForInngaaendeDokument(),
            RelevantDatoObjectMother.giveMeDatoForUtgaaendeDokument()
        ),
        dokumenter: List<GraphQLDokumentInfo?>? = listOf(DokumentInfoObjectMother.giveMeDokumentMedArkivertVariant())
    ) = GraphQLJournalpost(
        tittel,
        journalpostId,
        journalposttype,
        avsender,
        mottaker,
        relevanteDatoer,
        dokumenter
    )

    fun giveMeOneUtgaaendeDokument() =
        giveMeOneInngaaendeDokument(journalposttype = GraphQLJournalposttype.U)

}
