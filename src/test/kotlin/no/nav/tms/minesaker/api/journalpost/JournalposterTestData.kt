package no.nav.tms.minesaker.api.journalpost

import no.nav.dokument.saf.selvbetjening.generated.dto.AlleJournalposter
import no.nav.dokument.saf.selvbetjening.generated.dto.allejournalposter.Dokumentoversikt
import no.nav.dokument.saf.selvbetjening.generated.dto.allejournalposter.*
import no.nav.dokument.saf.selvbetjening.generated.dto.enums.*
import no.nav.tms.minesaker.api.journalpost.JournalpostTestData.listOfSakstemaer
import no.nav.tms.minesaker.api.journalpost.query.GraphQLError
import no.nav.tms.minesaker.api.journalpost.query.GraphQLResponse

object JournalpostTestData {

    fun inngaaendeDokument(
        tittel: String? = "Dummytittel Inngående",
        journalpostId: String = "dummyId-Inngående",
        journalposttype: Journalposttype = Journalposttype.I,
        temakode: String = "AAP",
        avsender: AvsenderMottaker? = avsenderMottakerPerson("123"),
        mottaker: AvsenderMottaker? = avsenderMottakerOrganisasjon("654"),
        sorteringsdato: String = "2018-01-01T12:00:00",
        dokumenter: List<DokumentInfo?>? = listOf(
            DokumentInfo(
                "Dummytittel med arkivert",
                "dummyId001",
                listOf(
                    Dokumentvariant(Variantformat.SLADDET, true, listOf("Skannet_dokument"), "PDF", 1_000_000),
                    Dokumentvariant(Variantformat.ARKIV, true, listOf("ok"), "PDF", 1_000_000)
                )
            )
        )
    ) = Journalpost(
        tittel,
        journalpostId,
        journalposttype,
        temakode,
        avsender,
        mottaker,
        sorteringsdato,
        dokumenter
    )


    fun avsenderMottakerPerson(
        navn: String = "Navn Navnesen"
    ) =
        AvsenderMottaker(navn)

    fun avsenderMottakerOrganisasjon(
        navn: String = "Navn Navnesen"
    ) =
        AvsenderMottaker(navn)

    fun listOfSakstemaer(): List<SafJournalpost> {
        return listOf(
            inngaaendeDokument(temakode = "AAP"),
            inngaaendeDokument(temakode = "KON", journalposttype = Journalposttype.U)
        )
    }
}

private typealias SafJournalpost = no.nav.dokument.saf.selvbetjening.generated.dto.allejournalposter.Journalpost

object AlleJournalposterResultTestData {

    fun responseWithDataAndError(): GraphQLResponse<AlleJournalposter.Result> {
        val data = journalposterResult()
        val error = GraphQLError("Feilet ved henting av data for bruker.")
        return GraphQLResponse(data, listOf(error))
    }

    fun journalposterResult(): AlleJournalposter.Result {
        val temaer = listOfSakstemaer()
        val dokumentoversikt = Dokumentoversikt(temaer)
        return AlleJournalposter.Result(dokumentoversikt)
    }

}
