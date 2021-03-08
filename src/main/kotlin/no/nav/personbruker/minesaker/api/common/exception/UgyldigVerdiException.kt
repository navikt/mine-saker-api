package no.nav.personbruker.minesaker.api.common.exception

class UgyldigVerdiException(message: String, cause: Throwable?) : AbstractMineSakerException(message, cause) {

    constructor(message: String) : this(message, null)

}
