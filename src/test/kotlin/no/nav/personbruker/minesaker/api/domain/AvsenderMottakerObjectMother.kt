package no.nav.personbruker.minesaker.api.domain

object AvsenderMottakerObjectMother {

    fun giveMeInnloggetBrukerAsAvsender() : Dokumentkilde {
        return Dokumentkilde(true, DokumentkildeType.PERSON)
    }

    fun giveMeOrganisasjonSomAvsedner() : Dokumentkilde {
        return Dokumentkilde(false, DokumentkildeType.ORGANISASJON)
    }

}