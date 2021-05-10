package no.nav.personbruker.minesaker.api.saf.domain.objectmothers

import no.nav.personbruker.minesaker.api.saf.domain.AvsenderMottaker
import no.nav.personbruker.minesaker.api.saf.domain.AvsenderMottakerType

object AvsenderMottakerObjectMother {

    fun giveMeInnloggetBrukerAsAvsender() : AvsenderMottaker {
        return AvsenderMottaker(true, AvsenderMottakerType.PERSON)
    }

    fun giveMeOrganisasjonSomAvsedner() : AvsenderMottaker {
        return AvsenderMottaker(false, AvsenderMottakerType.ORGANISASJON)
    }

}