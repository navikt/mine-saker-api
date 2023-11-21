package no.nav.tms.minesaker.api.sak

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
