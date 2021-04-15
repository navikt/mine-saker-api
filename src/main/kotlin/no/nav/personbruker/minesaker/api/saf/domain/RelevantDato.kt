package no.nav.personbruker.minesaker.api.saf.domain

import java.time.ZonedDateTime

data class RelevantDato(
    val dato : ZonedDateTime,
    val type : Datotype
)

fun List<RelevantDato>.plukkUtNyesteDato(): RelevantDato {
    return maxByOrNull { it.dato } ?: throw Exception("Klarte ikke å avgjøre hva som er nyeste dato")
}
