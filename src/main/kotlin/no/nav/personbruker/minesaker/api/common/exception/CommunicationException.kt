package no.nav.personbruker.minesaker.api.common.exception

import io.ktor.http.*

class CommunicationException(
    message: String,
    private val status: HttpStatusCode,
) : AbstractMineSakerException(message) {

    override fun toString(): String {
        return "${super.toString()}, status=$status)"
    }

}
