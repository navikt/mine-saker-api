package no.nav.tms.minesaker.api.saf.sakstemaer

object SakstemaTestData {

    fun sakstema(
        navn: String = "navn",
        kode: String = "FOR",
        journalposter: List<SafJournalpost> = listOf(inngaaendeDokument())
    ): SafSakstema {
        return SafSakstema(navn, kode, journalposter)
    }

    fun inngaaendeDokument(
        relevanteDatoer: List<SafRelevantDato?> = listOf(
            datoForInngaaendeDokument(),
            datoForUtgaaendeDokument()
        ),
    ) = SafJournalpost(relevanteDatoer)

    fun datoForUtgaaendeDokument(): SafRelevantDato {
        return SafRelevantDato("2018-06-01T12:00:00")
    }

    fun datoForInngaaendeDokument(): SafRelevantDato {
        return SafRelevantDato("2018-04-02T12:00:00")
    }

    fun datoForNotat(): SafRelevantDato {
        return SafRelevantDato("2018-05-03T12:00:00")
    }

}
