package no.nav.personbruker.minesaker.api.saf.domain

import java.time.ZonedDateTime

data class Dato(
    val dato : ZonedDateTime,
    val type : Datotype
)

fun List<Dato>.plukkUtNyesteDato(): Dato {
    return maxByOrNull { it.dato } ?: throw Exception("Klarte ikke å avgjøre hva som er nyeste dato")
}
