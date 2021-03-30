package no.nav.personbruker.minesaker.api.common.exception

class UnknownValueException(
    val feltnavn: String
) : AbstractMineSakerException("Mottok en ukjent feltverdi fra SAF.") {

    override fun toString(): String {
        return "${super.toString()}, feltnavn='$feltnavn'"
    }

}
