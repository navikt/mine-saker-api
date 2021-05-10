package no.nav.personbruker.minesaker.api.domain

object DokumentinfoObjectMother {

    fun giveMeDokumentListeMedEtVedlegg(): List<Dokumentinfo> {
        return listOf(
            giveMeHoveddokument(),
            giveMeHoveddokument(
                tittel = Tittel("Vedlegg1"),
                type = Dokumenttype.VEDLEGG
            )
        )
    }

    fun giveMeHoveddokument(
        tittel: Tittel = Tittel("Hoveddokument"),
        id: DokumentInfoId = DokumentInfoId("dummyId001"),
        type: Dokumenttype = Dokumenttype.HOVED,
        brukerHarTiltang: Boolean = true,
        grunnerTilIkkeTilgang: List<String> = listOf("ok")
    ): Dokumentinfo = Dokumentinfo(
        tittel,
        id,
        type,
        brukerHarTiltang,
        grunnerTilIkkeTilgang
    )


}
