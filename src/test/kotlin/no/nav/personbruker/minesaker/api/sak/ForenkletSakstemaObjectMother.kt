package no.nav.personbruker.minesaker.api.sak

import no.nav.personbruker.minesaker.api.domain.ForenkletSakstema
import no.nav.personbruker.minesaker.api.domain.Navn
import no.nav.personbruker.minesaker.api.domain.Sakstemakode
import java.time.ZonedDateTime

object ForenkletSakstemaObjectMother {

    fun giveMeDagpengerResult(sisteEndret: ZonedDateTime = ZonedDateTime.now()) = ForenkletSakstema(
        Navn("Dagpenger"),
        Sakstemakode.DAG,
        sisteEndret
    )

    fun giveMePensjonResult(sisteEndret: ZonedDateTime = ZonedDateTime.now()) = ForenkletSakstema(
        Navn("Pensjon"),
        Sakstemakode.PEN,
        sisteEndret
    )

    fun giveMeOkonomiskSosialhjelpResult(sisteEndret: ZonedDateTime = ZonedDateTime.now()) = ForenkletSakstema(
        Navn("Økonomisk sosialhjelp"),
        Sakstemakode.KOM,
        sisteEndret
    )

}
