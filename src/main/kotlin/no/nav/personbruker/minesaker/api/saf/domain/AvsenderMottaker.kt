package no.nav.personbruker.minesaker.api.saf.domain

data class AvsenderMottaker(
    val innloggetBrukerErAvsender: Boolean,
    val type: AvsenderMottakerType
)
