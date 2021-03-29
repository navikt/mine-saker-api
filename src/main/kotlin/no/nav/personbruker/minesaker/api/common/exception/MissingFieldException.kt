package no.nav.personbruker.minesaker.api.common.exception

class MissingFieldException(
    val feltnavn: String
) : AbstractMineSakerException("Et etterspurt felt ble ikke sendt med.") {

    override fun toString(): String {
        return "${super.toString()}, feltnavn='$feltnavn'"
    }

}
