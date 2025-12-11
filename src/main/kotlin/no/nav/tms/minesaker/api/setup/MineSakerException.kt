package no.nav.tms.minesaker.api.setup

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

class PrematureClientCloseException(
    cause: Throwable,
    private val journalpostId: String,
    private val dokumentId: String,
    private val fileType: String,
    private val fileSize: Long
) : RuntimeException(cause) {
    fun describe() = "Streaming av dokument til bruker avbrutt [journalpostId: $journalpostId, dokumentId: $dokumentId, filtype: $fileType, størrelse: ${formatBytes(fileSize)}]"

    private fun formatBytes(bytes: Long) = when(bytes) {
        in 0 until KILOBYTE -> "$bytes bytes"
        in KILOBYTE until MEGABYTE -> "%.1f kB".format(bytes.toDouble() / KILOBYTE.toDouble())
        in MEGABYTE until GIGABYTE  -> "%.1f MB".format(bytes.toDouble() / MEGABYTE.toDouble())
        in GIGABYTE..Long.MAX_VALUE  -> "%.1f GB".format(bytes.toDouble() / GIGABYTE.toDouble())
        else -> "$bytes bytes (err)"
    }

    companion object {
        private const val KILOBYTE = 1024L
        private const val MEGABYTE = KILOBYTE * 1024
        private const val GIGABYTE = MEGABYTE * 1024
    }
}


class CommunicationException(message: String, cause: Throwable? = null, sensitiveMessage: String? = null) :
    MineSakerException(message, cause, sensitiveMessage)

class DocumentNotFoundException(
    message: String,
    val journalpostId: String,
    val dokumentinfoId: String,
    cause: Throwable? = null,
    sensitiveMessage: String? = null
) : MineSakerException(message, cause, sensitiveMessage)

class DocumentFormatNotAvailableException(
    message: String,
    val journalpostId: String,
    val dokumentinfoId: String,
    val requestedVariant: String,
) : MineSakerException(message)

class InvalidRequestException(message: String, cause: Throwable? = null) :
    MineSakerException(message, cause, null)

class SafResultException(
    message: String,
    val errors: List<GraphQLClientError>?,
    val extensions: Map<String, Any?>?
) : MineSakerException(message) {
    override fun toString(): String = "${super.toString()}, errors=$errors, extensions=$extensions)"
}
