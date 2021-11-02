package no.nav.personbruker.minesaker.api.domain

import java.time.ZonedDateTime

data class SistEndredeSakstemaer(
    val sistEndrede : List<ForenkletSakstema>,
    val dagpengerSistEndret : ZonedDateTime?
)
