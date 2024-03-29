package no.nav.tms.minesaker.api.saf.sakstemaer.transformers

import no.nav.tms.minesaker.api.saf.common.transformers.finnSistEndret
import no.nav.tms.minesaker.api.saf.common.transformers.parseAsUtcZonedDateTime
import java.time.ZonedDateTime

fun List<GraphQLRelevantDato?>.toInternal(): ZonedDateTime {
    val internal = filterNotNull().map { external -> external.toInternal() }
    return internal.finnSistEndret()
}

fun GraphQLRelevantDato.toInternal(): ZonedDateTime = dato.parseAsUtcZonedDateTime()
