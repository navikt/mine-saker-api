package no.nav.personbruker.minesaker.api.common.exception

class MissingFieldException(
    val feltnavn: String
) : SafException("Et etterspurt felt ble ikke sendt med fra SAF.") {

    override fun toString(): String {
        return "${super.toString()}, feltnavn='$feltnavn'"
    }

}
