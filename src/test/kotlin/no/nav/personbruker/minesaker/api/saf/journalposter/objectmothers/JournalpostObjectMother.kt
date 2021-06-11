package no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter

object JournalpostObjectMother {

    fun giveMeOneInngaaendeDokument(
        tittel: String? = "Dummytittel Inngående",
        journalpostId: String = "dummyId-Inngående",
        journalposttype: HentJournalposter.Journalposttype? = HentJournalposter.Journalposttype.I,
        avsender: HentJournalposter.AvsenderMottaker? = AvsenderMottakerObjectMother.giveMePerson("123"),
        mottaker: HentJournalposter.AvsenderMottaker? = AvsenderMottakerObjectMother.giveMeOrganisasjon("654"),
        relevanteDatoer: List<HentJournalposter.RelevantDato?> = listOf(
            RelevantDatoObjectMother.giveMeDatoForInngaaendeDokument(),
            RelevantDatoObjectMother.giveMeDatoForUtgaaendeDokument()
        ),
        dokumenter: List<HentJournalposter.DokumentInfo?>? = listOf(DokumentInfoObjectMother.giveMeDokumentMedArkivertVariant())
    ) = HentJournalposter.Journalpost(
        tittel,
        journalpostId,
        journalposttype,
        avsender,
        mottaker,
        relevanteDatoer,
        dokumenter
    )

    fun giveMeOneUtgaaendeDokument() =
        giveMeOneInngaaendeDokument(journalposttype = HentJournalposter.Journalposttype.U)

}
