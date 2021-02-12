package no.nav.personbruker.minesaker.api.saf.dto.`in`.objectmother

import no.nav.personbruker.minesaker.api.saf.dto.`in`.SafResultWrapper

object SafResultWrapperObjectMother {

    fun giveMeOneResult(): SafResultWrapper {
        return SafResultWrapper(
            ResponseDataObjectMother.giveMeOneResult()
        )
    }

}
