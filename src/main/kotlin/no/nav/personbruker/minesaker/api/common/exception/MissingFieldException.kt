package no.nav.personbruker.minesaker.api.common.exception

class MissingFieldException(
    feltnavn: String
) : SafException("Et etterspurt felt ble ikke sendt med fra SAF.") {

    init {
        this.addContext("feltnavn", feltnavn)
    }

}
