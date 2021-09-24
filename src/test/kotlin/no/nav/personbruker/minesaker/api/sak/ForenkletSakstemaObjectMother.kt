package no.nav.personbruker.minesaker.api.sak

import no.nav.personbruker.minesaker.api.domain.ForenkletSakstema
import no.nav.personbruker.minesaker.api.domain.Navn
import no.nav.personbruker.minesaker.api.domain.Sakstemakode
import java.net.URL
import java.time.ZonedDateTime

object ForenkletSakstemaObjectMother {

    fun giveMeDagpengerResult(sisteEndret: ZonedDateTime = ZonedDateTime.now()) = ForenkletSakstema(
        Navn("Dagpenger"),
        Sakstemakode.DAG,
        sisteEndret,
        URL("https://dummy/DAG")
    )

    fun giveMePensjonResult(sisteEndret: ZonedDateTime = ZonedDateTime.now()) = ForenkletSakstema(
        Navn("Pensjon"),
        Sakstemakode.PEN,
        sisteEndret,
        URL("https://dummy/PEN")
    )

    fun giveMeOkonomiskSosialhjelpResult(sisteEndret: ZonedDateTime = ZonedDateTime.now()) = ForenkletSakstema(
        Navn("Økonomisk sosialhjelp"),
        Sakstemakode.KOM,
        sisteEndret,
        URL("https://dummy/KOM")
    )

}
