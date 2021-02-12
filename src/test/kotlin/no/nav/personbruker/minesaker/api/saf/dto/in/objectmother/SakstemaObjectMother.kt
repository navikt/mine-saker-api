package no.nav.personbruker.minesaker.api.saf.dto.`in`.objectmother

import no.nav.personbruker.minesaker.api.saf.dto.`in`.Sakstema

object SakstemaObjectMother {

    fun giveMeForeldrepenger() : Sakstema {
        return Sakstema("Foreldrepenger", "FOR")
    }

}