package no.nav.tms.minesaker.api.saf.journalposter.v1

fun SafAvsenderMottaker.toInternal(innloggetBruker: String) = Dokumentkilde(
    innloggetBrukerErSelvKilden = innloggetBrukerErAvsender(innloggetBruker),
    type.toInternal()
)

fun SafAvsenderMottaker.innloggetBrukerErAvsender(innloggetBruker: String) =
    type == SafAvsenderMottakerIdType.FNR && id == innloggetBruker

fun SafAvsenderMottakerIdType.toInternal(): DokumentkildeType {
    return when (this) {
        SafAvsenderMottakerIdType.FNR -> DokumentkildeType.PERSON
        SafAvsenderMottakerIdType.HPRNR -> DokumentkildeType.HELSEPERSONELL
        SafAvsenderMottakerIdType.ORGNR -> DokumentkildeType.ORGANISASJON
        SafAvsenderMottakerIdType.UTL_ORG -> DokumentkildeType.ORGANISASJON
        SafAvsenderMottakerIdType.NULL -> DokumentkildeType.UKJENT
        SafAvsenderMottakerIdType.UKJENT -> DokumentkildeType.UKJENT
        SafAvsenderMottakerIdType.__UNKNOWN_VALUE -> DokumentkildeType.UKJENT
    }
}
