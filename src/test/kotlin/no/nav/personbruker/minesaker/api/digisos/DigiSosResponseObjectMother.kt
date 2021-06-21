package no.nav.personbruker.minesaker.api.digisos

import java.time.LocalDateTime

object DigiSosResponseObjectMother {

    fun giveMeResponseSisteEndretEnUkeSiden() = DigiSosResponse(
        "Økonomisk sosialhjelp",
        "KOM",
        LocalDateTime.now().minusWeeks(1)
    )

}
