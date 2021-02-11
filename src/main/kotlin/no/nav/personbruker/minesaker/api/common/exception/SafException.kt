package no.nav.personbruker.minesaker.api.common.exception

class SafException(message: String, cause: Throwable?) : AbstractMineSakerException(message, cause) {

    constructor(message: String) : this(message, null)

}
