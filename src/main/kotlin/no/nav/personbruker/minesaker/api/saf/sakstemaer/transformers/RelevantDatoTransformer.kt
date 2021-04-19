package no.nav.personbruker.minesaker.api.saf.sakstemaer.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer
import no.nav.personbruker.minesaker.api.saf.common.transformers.finnSistEndret
import no.nav.personbruker.minesaker.api.saf.common.transformers.toInternal
import java.time.ZonedDateTime

fun List<HentSakstemaer.RelevantDato?>.toInternal(): ZonedDateTime {
    val internal = filterNotNull().map { external -> external.toInternal() }
    return internal.finnSistEndret()
}

fun HentSakstemaer.RelevantDato.toInternal() : ZonedDateTime = dato.toInternal()
