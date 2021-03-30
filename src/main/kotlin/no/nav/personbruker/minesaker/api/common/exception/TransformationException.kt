package no.nav.personbruker.minesaker.api.common.exception

class TransformationException(
    message: String,
    val type: ErrorType,
    cause: Throwable?
) :
    AbstractMineSakerException(message, cause) {

    constructor(message: String, type : ErrorType) : this(message, type, null)

    enum class ErrorType {
        MISSING_FIELD,
        UNKNOWN_VALUE,
    }

    override fun toString(): String {
        return super.toString() + ", errorType: $type"
    }

}
