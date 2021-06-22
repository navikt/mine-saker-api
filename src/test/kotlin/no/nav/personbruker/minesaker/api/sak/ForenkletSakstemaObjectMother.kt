package no.nav.personbruker.minesaker.api.sak

import no.nav.personbruker.minesaker.api.domain.ForenkletSakstema
import no.nav.personbruker.minesaker.api.domain.Navn
import no.nav.personbruker.minesaker.api.domain.Sakstemakode
import java.time.ZonedDateTime

object ForenkletSakstemaObjectMother {

    fun giveMeDagpengerResult() = ForenkletSakstema(
        Navn("Dagpenger"),
        Sakstemakode.DAG,
        ZonedDateTime.now()
    )

    fun giveMePensjonResult() = ForenkletSakstema(
        Navn("Pensjon"),
        Sakstemakode.PEN,
        ZonedDateTime.now()
    )

    fun giveMeOkonomiskSosialhjelpResult() = ForenkletSakstema(
        Navn("Økonomisk sosialhjelp"),
        Sakstemakode.KOM,
        ZonedDateTime.now()
    )

}
