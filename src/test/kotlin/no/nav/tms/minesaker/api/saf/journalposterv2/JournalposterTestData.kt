package no.nav.tms.minesaker.api.saf.journalposterv2

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.tms.minesaker.api.saf.GraphQLError
import no.nav.tms.minesaker.api.saf.GraphQLResponse
import no.nav.tms.minesaker.api.saf.journalposter.JournalpostTestData.listOfSakstemaer
import no.nav.tms.minesaker.api.saf.journalposter.v1.*

object JournalpostTestData {

    fun inngaaendeDokument(
        tittel: String? = "Dummytittel Inngående",
        journalpostId: String = "dummyId-Inngående",
        journalposttype: SafJournalposttype = SafJournalposttype.I,
        avsender: SafAvsenderMottaker? = avsenderMottakerPerson("123"),
        mottaker: SafAvsenderMottaker? = avsenderMottakerOrganisasjon("654"),
        relevanteDatoer: List<SafRelevantDato?> = listOf(
            RelevantDatoTestData.datoForInngaaendeDokument(),
            RelevantDatoTestData.datoForUtgaaendeDokument()
        ),
        dokumenter: List<SafDokumentInfo?>? = listOf(
            SafDokumentInfo(
                "Dummytittel med arkivert",
                "dummyId001",
                listOf(
                    SafDokumentvariant(SafVariantformat.SLADDET, true, listOf("Skannet_dokument"), "PDF"),
                    SafDokumentvariant(SafVariantformat.ARKIV, true, listOf("ok"), "PDF")
                )
            )
        )
    ) = SafJournalpost(
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
        idType: SafAvsenderMottakerIdType = SafAvsenderMottakerIdType.FNR
    ) =
        SafAvsenderMottaker(ident, idType)

    fun avsenderMottakerOrganisasjon(
        ident: String = "987654",
        idType: SafAvsenderMottakerIdType = SafAvsenderMottakerIdType.ORGNR
    ) =
        SafAvsenderMottaker(ident, idType)

    fun listOfSakstemaer(): List<SafSakstema> {
        return listOf(
            sakstemaWithUtgaaendeDokument(),
            sakstemaWithInngaaendeDokument()
        )
    }

    fun sakstemaWithUtgaaendeDokument(navn: String = "navn1", kode: String = "AAP") =
        SafSakstema(navn, kode, listOf(inngaaendeDokument()))

    fun sakstemaWithInngaaendeDokument(navn: String = "navn2", kode: String = "KON") =
        SafSakstema(
            navn,
            kode,
            listOf(inngaaendeDokument(journalposttype = SafJournalposttype.U))
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
        val dokumentoversikt = SafDokumentoversikt(temaer)
        return HentJournalposter.Result(dokumentoversikt)
    }

}

object RelevantDatoTestData {

    fun datoForUtgaaendeDokument(): SafRelevantDato {
        return SafRelevantDato("2018-01-01T12:00:00", SafDatotype.DATO_EKSPEDERT)
    }

    fun datoForInngaaendeDokument(): SafRelevantDato {
        return SafRelevantDato("2018-02-02T12:00:00", SafDatotype.DATO_REGISTRERT)
    }

    fun datoForNotat(): SafRelevantDato {
        return SafRelevantDato("2018-03-03T12:00:00", SafDatotype.DATO_OPPRETTET)
    }
}
