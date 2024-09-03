package no.nav.tms.minesaker.api.saf.sakstemaer

import no.nav.tms.minesaker.api.setup.MineSakerException

class SakstemaException(
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

        fun withMissingFieldName(fieldName: String): SakstemaException {
            val msg = "Et etterspurt felt ble ikke sendt med"
            val te = SakstemaException(msg, ErrorType.MISSING_FIELD)

            te.addContext(feltnavnKey, fieldName)
            return te
        }
    }

}
