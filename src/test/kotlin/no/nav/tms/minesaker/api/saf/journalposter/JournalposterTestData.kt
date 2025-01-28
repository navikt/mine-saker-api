package no.nav.tms.minesaker.api.saf.journalposter

import no.nav.dokument.saf.selvbetjening.generated.dto.AlleJournalposter
import no.nav.dokument.saf.selvbetjening.generated.dto.allejournalposter.Dokumentoversikt
import no.nav.dokument.saf.selvbetjening.generated.dto.allejournalposter.*
import no.nav.dokument.saf.selvbetjening.generated.dto.enums.*
import no.nav.tms.minesaker.api.saf.GraphQLError
import no.nav.tms.minesaker.api.saf.GraphQLResponse
import no.nav.tms.minesaker.api.saf.journalposter.JournalpostTestData.listOfSakstemaer

object JournalpostTestData {

    fun inngaaendeDokument(
        tittel: String? = "Dummytittel Inngående",
        journalpostId: String = "dummyId-Inngående",
        journalposttype: Journalposttype = Journalposttype.I,
        temakode: String = "AAP",
        avsender: AvsenderMottaker? = avsenderMottakerPerson("123"),
        mottaker: AvsenderMottaker? = avsenderMottakerOrganisasjon("654"),
        relevanteDatoer: List<RelevantDato?> = listOf(
            RelevantDatoTestData.datoOpprettet(),
            RelevantDatoTestData.datoForInngaaendeDokument(),
            RelevantDatoTestData.datoForUtgaaendeDokument()
        ),
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
        relevanteDatoer,
        dokumenter
    )


    fun avsenderMottakerPerson(
        ident: String = "123",
        idType: AvsenderMottakerIdType = AvsenderMottakerIdType.FNR,
        navn: String = "Navn Navnesen"
    ) =
        AvsenderMottaker(ident, idType, navn)

    fun avsenderMottakerOrganisasjon(
        ident: String = "987654",
        idType: AvsenderMottakerIdType = AvsenderMottakerIdType.ORGNR,
        navn: String = "Navn Navnesen"
    ) =
        AvsenderMottaker(ident, idType, navn)

    fun listOfSakstemaer(): List<Journalpost> {
        return listOf(
            inngaaendeDokument(temakode = "AAP"),
            inngaaendeDokument(temakode = "KON", journalposttype = Journalposttype.U)
        )
    }
}

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

object RelevantDatoTestData {

    fun datoOpprettet() = RelevantDato("2018-01-01T12:00:00", Datotype.DATO_OPPRETTET)

    fun datoForUtgaaendeDokument() = RelevantDato("2018-01-01T12:00:00", Datotype.DATO_EKSPEDERT)

    fun datoForInngaaendeDokument() = RelevantDato("2018-02-02T12:00:00", Datotype.DATO_REGISTRERT)
}
