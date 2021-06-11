package no.nav.personbruker.minesaker.api.domain

data class Dokumentkilde(
    val innloggetBrukerErSelvKilden: Boolean,
    val type: DokumentkildeType
)
