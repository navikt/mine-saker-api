package no.nav.personbruker.minesaker.api.exception

class TransformationException(
    message: String,
    val type: ErrorType,
    cause: Throwable?
) : MineSakerException(message, cause) {

    constructor(message: String, type: ErrorType) : this(message, type, null)

    enum class ErrorType {
        INVALID_STATE,
        MISSING_FIELD,
        UNKNOWN_VALUE,
    }

    override fun toString(): String {
        return super.toString() + ", errorType: $type"
    }

    companion object {
        const val feltnavnKey = "feltnavn"

        fun withMissingFieldName(fieldName: String): TransformationException {
            val msg = "Et etterspurt felt ble ikke sendt med"
            val te = TransformationException(msg, ErrorType.MISSING_FIELD)

            te.addContext(feltnavnKey, fieldName)
            return te
        }
    }

}
