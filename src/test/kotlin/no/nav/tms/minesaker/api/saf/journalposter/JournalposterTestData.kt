package no.nav.tms.minesaker.api.saf.journalposter

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.tms.minesaker.api.saf.common.GraphQLError
import no.nav.tms.minesaker.api.saf.common.GraphQLResponse
import no.nav.tms.minesaker.api.saf.journalposter.JournalpostTestData.listOfSakstemaer
import no.nav.tms.minesaker.api.saf.journalposter.transformers.*

object JournalpostTestData {

    fun inngaaendeDokument(
        tittel: String? = "Dummytittel Inngående",
        journalpostId: String = "dummyId-Inngående",
        journalposttype: GraphQLJournalposttype = GraphQLJournalposttype.I,
        avsender: GraphQLAvsenderMottaker? = avsenderMottakerPerson("123"),
        mottaker: GraphQLAvsenderMottaker? = avsenderMottakerOrganisasjon("654"),
        relevanteDatoer: List<GraphQLRelevantDato?> = listOf(
            RelevantDatoTestData.datoForInngaaendeDokument(),
            RelevantDatoTestData.datoForUtgaaendeDokument()
        ),
        dokumenter: List<GraphQLDokumentInfo?>? = listOf(
            GraphQLDokumentInfo(
                "Dummytittel med arkivert",
                "dummyId001",
                listOf(
                    GraphQLDokumentvariant(GraphQLVariantformat.SLADDET, true, listOf("Skannet_dokument"), "PDF"),
                    GraphQLDokumentvariant(GraphQLVariantformat.ARKIV, true, listOf("ok"), "PDF")
                )
            )
        )
    ) = GraphQLJournalpost(
        tittel,
        journalpostId,
        journalposttype,
        avsender,
        mottaker,
        relevanteDatoer,
        dokumenter
    )


    fun avsenderMottakerPerson(
        ident: String = "123",
        idType: GraphQLAvsenderMottakerIdType = GraphQLAvsenderMottakerIdType.FNR
    ) =
        GraphQLAvsenderMottaker(ident, idType)

    fun avsenderMottakerOrganisasjon(
        ident: String = "987654",
        idType: GraphQLAvsenderMottakerIdType = GraphQLAvsenderMottakerIdType.ORGNR
    ) =
        GraphQLAvsenderMottaker(ident, idType)

    fun listOfSakstemaer(): List<GraphQLSakstema> {
        return listOf(
            sakstemaWithUtgaaendeDokument(),
            sakstemaWithInngaaendeDokument()
        )
    }

    fun sakstemaWithUtgaaendeDokument(navn: String = "navn1", kode: String = "AAP") =
        GraphQLSakstema(navn, kode, listOf(inngaaendeDokument()))

    fun sakstemaWithInngaaendeDokument(navn: String = "navn2", kode: String = "KON") =
        GraphQLSakstema(
            navn,
            kode,
            listOf(inngaaendeDokument(journalposttype = GraphQLJournalposttype.U))
        )
}


object HentJournalposterResultTestData {

    fun responseWithDataAndError(): GraphQLResponse<HentJournalposter.Result> {
        val data = journalposterResult()
        val error = GraphQLError("Feilet ved henting av data for bruker.")
        return GraphQLResponse(data, listOf(error))
    }

    fun journalposterResult(): HentJournalposter.Result {
        val temaer = listOfSakstemaer()
        val dokumentoversikt = GraphQLDokumentoversikt(temaer)
        return HentJournalposter.Result(dokumentoversikt)
    }

}

object RelevantDatoTestData {

    fun datoForUtgaaendeDokument(): GraphQLRelevantDato {
        return GraphQLRelevantDato("2018-01-01T12:00:00", GraphQLDatotype.DATO_EKSPEDERT)
    }

    fun datoForInngaaendeDokument(): GraphQLRelevantDato {
        return GraphQLRelevantDato("2018-02-02T12:00:00", GraphQLDatotype.DATO_REGISTRERT)
    }

    fun datoForNotat(): GraphQLRelevantDato {
        return GraphQLRelevantDato("2018-03-03T12:00:00", GraphQLDatotype.DATO_OPPRETTET)
    }
}
