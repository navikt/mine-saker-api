package no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter

object JournalpostObjectMother {

    fun giveMeOneInngaaendeDokument(
        tittel: String? = "Dummytittel Inngående",
        journalpostId: String = "dummyId-Inngående",
        journalposttype: HentJournalposter.Journalposttype? = HentJournalposter.Journalposttype.I,
        avsenderMottaker: HentJournalposter.AvsenderMottaker? = AvsenderMottakerObjectMother.giveMePersonSomAvsender("123"),
        relevanteDatoer: List<HentJournalposter.RelevantDato?> = listOf(
            RelevantDatoObjectMother.giveMeDatoForInngaaendeDokument(),
            RelevantDatoObjectMother.giveMeDatoForUtgaaendeDokument()
        ),
        journalposter: List<HentJournalposter.DokumentInfo?>? = listOf(DokumentInfoObjectMother.giveMeDokumentMedArkivertVariant())
    ) = HentJournalposter.Journalpost(
        tittel,
        journalpostId,
        journalposttype,
        avsenderMottaker,
        relevanteDatoer,
        journalposter
    )

    fun giveMeOneUtgaaendeDokument() =
        giveMeOneInngaaendeDokument(journalposttype = HentJournalposter.Journalposttype.U)

}
