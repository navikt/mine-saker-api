package no.nav.personbruker.minesaker.api.common.exception

open class DocumentNotFoundException(message: String, cause: Throwable?) : AbstractMineSakerException(message, cause) {

    constructor(message: String) : this(message, null)

}
