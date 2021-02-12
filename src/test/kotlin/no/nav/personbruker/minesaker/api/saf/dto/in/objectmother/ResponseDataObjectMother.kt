package no.nav.personbruker.minesaker.api.saf.dto.`in`.objectmother

import no.nav.personbruker.minesaker.api.saf.dto.`in`.ResponseData

object ResponseDataObjectMother {

    fun giveMeOneResult(): ResponseData {
        return ResponseData(
            DokumentoversiktSelvbetjeningObjectMother.giveMeDokumentoversikt()
        )
    }

}
