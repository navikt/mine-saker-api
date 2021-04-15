package no.nav.personbruker.minesaker.api.saf.common.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.DateTime
import java.time.ZonedDateTime

object DateTimeTransformer {
    val timeZoneUTC = "+00:00"
}

fun DateTime.toInternal(): ZonedDateTime = ZonedDateTime.parse("$this${DateTimeTransformer.timeZoneUTC}")
