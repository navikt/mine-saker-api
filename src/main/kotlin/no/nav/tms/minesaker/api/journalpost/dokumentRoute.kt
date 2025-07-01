package no.nav.tms.minesaker.api.journalpost

import no.nav.tms.minesaker.api.idportenUser

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import no.nav.tms.minesaker.api.setup.InvalidRequestException
import no.nav.tms.minesaker.api.fullmakt.FullmaktAttribute
import no.nav.tms.minesaker.api.fullmakt.enableFullmakt

private const val dokumentIdParameterName = "dokumentId"

fun Route.dokumentRoute(service: SafService) {

    get("/dokument/{$journalpostIdParameterName}/{$dokumentIdParameterName}") {
        service.hentDokumentStream(
            idportenUser,
            call.journalpostId(),
            call.dokumentInfoId(),
            call.sladdetDokument(),
        ) { stream ->

            call.respondBytesWriter(
                contentLength = stream.size,
                contentType = stream.contentType,
                status = HttpStatusCode.OK
            ) {
                streamFrom(stream.channel)
            }
        }
    }
}

private suspend fun ByteWriteChannel.streamFrom(input: ByteReadChannel) {
    while (!input.isClosedForRead) {
        val packet = input.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
        while (!packet.exhausted()) {
            writePacket(packet)
        }
        flush()
    }
}

private fun ApplicationCall.journalpostId(): String = parameters[journalpostIdParameterName]
    ?: throw InvalidRequestException("Kallet kan ikke utføres uten at '$journalpostIdParameterName' er spesifisert.")


private fun ApplicationCall.dokumentInfoId(): String = parameters[dokumentIdParameterName]
    ?.let {
        if (it == "-")
            throw InvalidRequestException(
                message = "Forsøkte å hente info for ugyldig dokumment-id",
                sensitiveMessage = "Forsøkte å hente info for ugyldig dokuemnt-id"
            )
        else it
    }
    ?: throw InvalidRequestException("Kallet kan ikke utføres uten at '$dokumentIdParameterName' er spesifisert.")

private fun ApplicationCall.sladdetDokument(): Boolean = try {
    request.queryParameters["sladdet"]?.toBoolean() ?: false
} catch (e: Exception) {
    throw InvalidRequestException("Ugyldig verdi for parameter 'sladdet'")
}
