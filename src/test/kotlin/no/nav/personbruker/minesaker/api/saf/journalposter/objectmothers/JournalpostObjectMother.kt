package no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter

object JournalpostObjectMother {

    fun giveMeOneInngaaendeDokument(): HentJournalposter.Journalpost {
        val relevanteDatoer = listOf(
            RelevantDatoObjectMother.giveMeDatoForInngaaendeDokument(),
            RelevantDatoObjectMother.giveMeDatoForUtgaaendeDokument()
        )
        return HentJournalposter.Journalpost(
            "Dummytittel Inngående",
            "dummyId-Inngående",
            HentJournalposter.Journalposttype.I,
            AvsenderMottakerObjectMother.giveMePersonSomAvsender("123"),
            relevanteDatoer,
            listOf(DokumentInfoObjectMother.giveMeDokumentMedArkivertVariant())
        )
    }

    fun giveMeOneUtgaaendeDokument(): HentJournalposter.Journalpost {
        val relevanteDatoer = listOf(
            RelevantDatoObjectMother.giveMeDatoForInngaaendeDokument(),
            RelevantDatoObjectMother.giveMeDatoForUtgaaendeDokument()
        )
        return HentJournalposter.Journalpost(
            "Dummytittel Utgående",
            "dummyId-Utgående",
            HentJournalposter.Journalposttype.U,
            AvsenderMottakerObjectMother.giveMePersonSomAvsender("123"),
            relevanteDatoer,
            listOf(DokumentInfoObjectMother.giveMeDokumentMedArkivertVariant())
        )
    }

    fun giveMeUtenTittel(): HentJournalposter.Journalpost {
        val relevanteDatoer = listOf(
            RelevantDatoObjectMother.giveMeDatoForInngaaendeDokument(),
            RelevantDatoObjectMother.giveMeDatoForUtgaaendeDokument()
        )
        return HentJournalposter.Journalpost(
            null,
            "dummyId-Utgående",
            HentJournalposter.Journalposttype.U,
            AvsenderMottakerObjectMother.giveMePersonSomAvsender("123"),
            relevanteDatoer,
            listOf(DokumentInfoObjectMother.giveMeDokumentMedArkivertVariant())
        )
    }

}
