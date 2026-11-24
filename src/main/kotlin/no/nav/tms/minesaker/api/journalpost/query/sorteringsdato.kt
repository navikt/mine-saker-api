package no.nav.tms.minesaker.api.journalpost.query

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

private val norskTidssone = ZoneId.of("Europe/Oslo")

internal fun sorteringsdato(datoSortering: String): ZonedDateTime =
    LocalDateTime.parse(datoSortering)
        .atZone(norskTidssone)
        .withZoneSameInstant(ZoneOffset.UTC)
