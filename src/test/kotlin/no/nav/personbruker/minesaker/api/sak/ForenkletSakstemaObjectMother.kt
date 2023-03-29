package no.nav.personbruker.minesaker.api.sak

import no.nav.personbruker.minesaker.api.domain.ForenkletSakstema
import no.nav.personbruker.minesaker.api.domain.Sakstemakode
import java.time.ZonedDateTime

object ForenkletSakstemaObjectMother {

    fun giveMeDagpengerResult(sisteEndret: ZonedDateTime = ZonedDateTime.now()) = ForenkletSakstema(
        "Dagpenger",
        Sakstemakode.DAG,
        sisteEndret,
        "https://dummy/DAG"
    )

    fun giveMePensjonResult(sisteEndret: ZonedDateTime = ZonedDateTime.now()) = ForenkletSakstema(
        "Pensjon",
        Sakstemakode.PEN,
        sisteEndret,
        "https://dummy/PEN"
    )

    fun giveMeOkonomiskSosialhjelpResult(sisteEndret: ZonedDateTime = ZonedDateTime.now()) = ForenkletSakstema(
        "Økonomisk sosialhjelp",
        Sakstemakode.KOM,
        sisteEndret,
        "https://dummy/KOM"
    )

}
