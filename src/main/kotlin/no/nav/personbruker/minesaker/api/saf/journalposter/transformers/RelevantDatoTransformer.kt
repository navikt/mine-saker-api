package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import java.time.ZonedDateTime

fun List<HentJournalposter.RelevantDato?>.toInternal(): ZonedDateTime {
    val internal = filterNotNull().map { external -> external.toInternal() }
    return internal.finnSistEndret()
}

fun HentJournalposter.RelevantDato.toInternal() = dato.toInternal()

fun List<ZonedDateTime>.finnSistEndret(): ZonedDateTime {
    return maxByOrNull { it } ?: throw Exception("Klarte ikke å avgjøre hva som er nyeste dato")
}
