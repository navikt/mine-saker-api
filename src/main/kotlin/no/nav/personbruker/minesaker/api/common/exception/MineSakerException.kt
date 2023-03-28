package no.nav.personbruker.minesaker.api.common.exception

import com.expediagroup.graphql.client.types.GraphQLClientError

open class MineSakerException(message: String, cause: Throwable?, val sensitiveMessage: String? = null) :
    Exception(message, cause) {

    constructor(message: String) : this(message, null)

    val context: MutableMap<String, Any?> = mutableMapOf()

    fun addContext(key: String, value: Any?): MineSakerException {
        context[key] = value
        return this
    }

    override fun toString(): String {
        return when (context.isNotEmpty()) {
            true -> super.toString() + ", context: $context"
            false -> super.toString()
        }
    }

}

class CommunicationException(message: String, cause: Throwable? = null, sensitiveMessage: String? = null) :
    MineSakerException(message, cause, sensitiveMessage)

class DocumentNotFoundException(message: String, cause: Throwable? = null) : MineSakerException(message, cause)

class InvalidRequestException(message: String, cause: Throwable? = null) : MineSakerException(message, cause)

class GraphQLResultException(
    message: String,
    internal val errors: List<GraphQLClientError>?,
    internal val extensions: Map<String, Any?>?
) : MineSakerException(message) {
    override fun toString(): String = "${super.toString()}, errors=$errors, extensions=$extensions)"
}
