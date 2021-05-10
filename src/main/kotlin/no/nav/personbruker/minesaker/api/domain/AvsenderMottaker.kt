package no.nav.personbruker.minesaker.api.domain

data class AvsenderMottaker(
    val innloggetBrukerErAvsender: Boolean,
    val type: AvsenderMottakerType
)
