package no.nav.personbruker.minesaker.api.sak

object SakstemaResultObjectMother {

    fun createSafResults(): SakstemaResult {
        val list = listOf(
            ForenkletSakstemaObjectMother.giveMeDagpengerResult(),
            ForenkletSakstemaObjectMother.giveMePensjonResult()
        )
        return SakstemaResult(list)
    }

    fun createDigiSosResults(): SakstemaResult {
        val list = listOf(
            ForenkletSakstemaObjectMother.giveMeOkonomiskSosialhjelpResult()
        )
        return SakstemaResult(list)
    }

    fun createDigiSosError(): SakstemaResult {
        return SakstemaResult(emptyList(), listOf(Kildetype.DIGISOS))
    }

    fun createResultWithOneError(): SakstemaResult {
        return createDigiSosError()
    }

    fun createResultWithTwoErrors(): SakstemaResult {
        return SakstemaResult(emptyList(), listOf(Kildetype.SAF, Kildetype.DIGISOS))
    }

}
