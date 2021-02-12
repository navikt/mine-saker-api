package no.nav.personbruker.minesaker.api.saf.dto.`in`.objectmother

import no.nav.personbruker.minesaker.api.saf.dto.`in`.DokumentoversiktSelvbetjening

object DokumentoversiktSelvbetjeningObjectMother {

    fun giveMeDokumentoversikt(): DokumentoversiktSelvbetjening {
        return DokumentoversiktSelvbetjening(
            listOf(
                SakstemaObjectMother.giveMeForeldrepenger()
            )
        )
    }

}