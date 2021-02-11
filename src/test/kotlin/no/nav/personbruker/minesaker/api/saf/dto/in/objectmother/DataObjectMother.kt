package no.nav.personbruker.minesaker.api.saf.dto.`in`.objectmother

import no.nav.personbruker.minesaker.api.saf.dto.`in`.Data

object DataObjectMother {

    fun giveMeOneResult(): Data {
        return Data(
            DokumentoversiktSelvbetjeningObjectMother.giveMeDokumentoversikt()
        )
    }

}
