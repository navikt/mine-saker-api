package no.nav.tms.minesaker.api.domain

data class Dokumentinfo(
    val tittel: String,
    val dokumentInfoId: String,
    val dokumenttype: Dokumenttype,
    val brukerHarTilgang: Boolean,
    val eventuelleGrunnerTilManglendeTilgang: List<String>,
    val variant: Dokumentvariant,
    val filtype: String
)

data class Dokumentkilde(
    val innloggetBrukerErSelvKilden: Boolean,
    val type: DokumentkildeType
)
enum class DokumentkildeType {
    PERSON,
    ORGANISASJON,
    HELSEPERSONELL,
    UKJENT
}

enum class Dokumenttype {
    HOVED,
    VEDLEGG
}
enum class Dokumentvariant {
    SLADDET,
    ARKIV
}
