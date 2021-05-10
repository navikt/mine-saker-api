package no.nav.personbruker.minesaker.api.domain

object AvsenderMottakerObjectMother {

    fun giveMeInnloggetBrukerAsAvsender() : AvsenderMottaker {
        return AvsenderMottaker(true, AvsenderMottakerType.PERSON)
    }

    fun giveMeOrganisasjonSomAvsedner() : AvsenderMottaker {
        return AvsenderMottaker(false, AvsenderMottakerType.ORGANISASJON)
    }

}