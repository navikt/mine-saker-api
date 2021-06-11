package no.nav.personbruker.minesaker.api.domain

import java.time.ZonedDateTime

object JournalpostObjectMother {

    fun giveMeJournalpostUtenVedlegg(): Journalpost {
        return giveMeJournalpostUtenDokumenter(
            tittel = Tittel("Uten vedlegg"),
            dokumenter = listOf(DokumentinfoObjectMother.giveMeHoveddokument())
        )
    }

    fun giveMeJournalpostMedVedlegg(): Journalpost {
        return giveMeJournalpostUtenDokumenter(
            tittel = Tittel("Med vedlegg"),
            dokumenter = DokumentinfoObjectMother.giveMeDokumentListeMedEtVedlegg()
        )
    }

    fun giveMeJournalpostUtenDokumenter(
        tittel: Tittel = Tittel ("Uten dokumenter"),
        id: JournalpostId = JournalpostId("dummyId001"),
        type: Journalposttype = Journalposttype.INNGAAENDE,
        avsender: Dokumentkilde = AvsenderMottakerObjectMother.giveMeOrganisasjonSomAvsedner(),
        mottaker: Dokumentkilde = AvsenderMottakerObjectMother.giveMeInnloggetBrukerAsAvsender(),
        sistEndret: ZonedDateTime = ZonedDateTime.now(),
        dokumenter: List<Dokumentinfo> = emptyList()
    ): Journalpost {
        return Journalpost(
            tittel,
            id,
            type,
            avsender,
            mottaker,
            sistEndret,
            dokumenter
        )
    }

}
