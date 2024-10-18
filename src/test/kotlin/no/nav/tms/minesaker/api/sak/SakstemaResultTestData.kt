package no.nav.tms.minesaker.api.sak

import no.nav.tms.minesaker.api.saf.sakstemaer.Kildetype
import no.nav.tms.minesaker.api.saf.sakstemaer.SakstemaResult

object SakstemaResultTestData {

    fun safResults(): SakstemaResult =
        SakstemaResult(
            listOf(
                ForenkletSakstemaTestData.dagpengerResult(),
                ForenkletSakstemaTestData.pensjonResult()
            )
        )


    fun createDigiSosResults(): SakstemaResult =
        SakstemaResult(
            listOf(
                ForenkletSakstemaTestData.okonomiskSosialhjelpResult()
            )
        )


    fun createDigiSosError(): SakstemaResult =
        SakstemaResult(emptyList(), listOf(Kildetype.DIGISOS))


}
