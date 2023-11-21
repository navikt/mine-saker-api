package no.nav.tms.minesaker.api.domain

import java.time.ZonedDateTime

object DomainTestData {

    fun journalpostUtenDokumenter(
        tittel: String = "Uten dokumenter",
        id: String = "dummyId001",
        type: Journalposttype = Journalposttype.INNGAAENDE,
        avsender: Dokumentkilde = Dokumentkilde(false, DokumentkildeType.ORGANISASJON),
        mottaker: Dokumentkilde = Dokumentkilde(true, DokumentkildeType.PERSON),
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

    fun hoveddokument(
        tittel: String = "Hoveddokument",
        id: String = "dummyId001",
        type: Dokumenttype = Dokumenttype.HOVED,
        brukerHarTiltang: Boolean = true,
        grunnerTilIkkeTilgang: List<String> = listOf("ok")
    ): Dokumentinfo = Dokumentinfo(
        tittel,
        id,
        type,
        brukerHarTiltang,
        grunnerTilIkkeTilgang,
        Dokumentvariant.SLADDET,
        "PDF"
    )

}



