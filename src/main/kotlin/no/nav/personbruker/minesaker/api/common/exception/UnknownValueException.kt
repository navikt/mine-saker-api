package no.nav.personbruker.minesaker.api.common.exception

class UnknownValueException(
    feltnavn: String
) : SafException("Mottok en ukjent feltverdi fra SAF.") {

    init {
        this.addContext("feltnavn", feltnavn)
    }

}
