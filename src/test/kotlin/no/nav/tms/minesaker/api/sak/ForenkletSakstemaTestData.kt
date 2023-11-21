package no.nav.tms.minesaker.api.sak

import no.nav.tms.minesaker.api.domain.ForenkletSakstema
import no.nav.tms.minesaker.api.domain.Sakstemakode
import java.time.ZonedDateTime

object ForenkletSakstemaTestData {

    fun dagpengerResult(sisteEndret: ZonedDateTime = ZonedDateTime.now()) = ForenkletSakstema(
        "Dagpenger",
        Sakstemakode.DAG,
        sisteEndret,
        "https://dummy/DAG"
    )

    fun pensjonResult(sisteEndret: ZonedDateTime = ZonedDateTime.now()) = ForenkletSakstema(
        "Pensjon",
        Sakstemakode.PEN,
        sisteEndret,
        "https://dummy/PEN"
    )

    fun okonomiskSosialhjelpResult(sisteEndret: ZonedDateTime = ZonedDateTime.now()) = ForenkletSakstema(
        "Økonomisk sosialhjelp",
        Sakstemakode.KOM,
        sisteEndret,
        "https://dummy/KOM"
    )

}
