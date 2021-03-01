package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.DateTime
import java.time.ZonedDateTime

object DateTimeTransformer {

    private val timeZoneUTC = "+00:00"

    fun toInternal(dato: DateTime): ZonedDateTime {
        return ZonedDateTime.parse("$dato$timeZoneUTC")
    }

}
