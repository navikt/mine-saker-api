package no.nav.personbruker.minesaker.api.common.exception

open class AbstractMineSakerException(message: String, cause: Throwable?) : Exception(message, cause) {

    constructor(message: String) : this(message, null)

    val context: MutableMap<String, Any> = mutableMapOf()

    fun addContext(key: String, value: Any) {
        context[key] = value
    }

    override fun toString(): String {
        return when (context.isNotEmpty()) {
            true -> super.toString() + ", context: $context"
            false -> super.toString()
        }
    }

}
