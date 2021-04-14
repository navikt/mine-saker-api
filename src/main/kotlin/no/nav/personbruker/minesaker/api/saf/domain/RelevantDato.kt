package no.nav.personbruker.minesaker.api.saf.domain

import java.time.ZonedDateTime

data class RelevantDato(
    val dato : ZonedDateTime,
    val type : Datotype
)
