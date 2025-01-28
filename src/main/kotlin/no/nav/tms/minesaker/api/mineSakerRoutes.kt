package no.nav.tms.minesaker.api


import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import no.nav.tms.minesaker.api.setup.InvalidRequestException
import no.nav.tms.minesaker.api.saf.fullmakt.FullmaktAttribute
import no.nav.tms.minesaker.api.saf.fullmakt.enableFullmakt
import no.nav.tms.minesaker.api.saf.journalposter.Sakstema

const val sakstemakode = "sakstemakode"
const val dokumentIdParameterName = "dokumentId"
const val journalpostIdParameterName = "journalpostId"

fun Route.mineSakerRoute(service: SakService) {

    enableFullmakt {

        get("/v2/journalposter/alle") {
            service.alleJournalposter(
                user = idportenUser,
                representert = call.representert
            ).let { result ->
                call.respond(HttpStatusCode.OK, result)
            }
        }

        get("/v2/journalposter/journalpost/{$journalpostIdParameterName}") {
            val journalpostId = call.journalpostId()

//            service.hentJournalpost(
//                user = idportenUser,
//                journapostId = journalpostId,
//                representert = call.representert
//            )?.let { result ->
//                call.respond(HttpStatusCode.OK, result)
//            } ?: run {
//                call.respondText("Fant ikke journalpost med id $journalpostId", status = HttpStatusCode.NotFound)
//            }

            val representert = if (call.enableRepr()) {
                call.representert
            } else {
                null
            }

            service.alleJournalposter(
                user = idportenUser,
                representert = representert
            ).firstOrNull{
                it.journalpostId == journalpostId
            }?.let { result ->
                call.respond(HttpStatusCode.OK, result)
            }?: run {
                call.respondText(
                    "Fant ikke journalpost med journalpostId $journalpostId",
                    status = HttpStatusCode.NotFound
                )
            }
        }
    }

    get("/v2/sosialhjelp/har_innsendte") {
        service.harInnsendteHosDigiSos(idportenUser).let {
            call.respond(it)
        }
    }

    get("/dokument/{$journalpostIdParameterName}/{$dokumentIdParameterName}") {
        service.hentDokumentStream(
            idportenUser,
            call.journalpostId(),
            call.dokumentInfoId()
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

// Lenkes til eller kalles fra andre steder enn dokumentarkiv
fun Route.sakApiExternal(
    service: SakService
) {
    get("/v2/journalposter/siste") {
        val antall = call.antallFromParameters() ?: 3

        call.respond(service.sisteJournalposter(idportenUser, antall))
    }
}


private val ApplicationCall.representert get() =
    attributes.getOrNull(FullmaktAttribute)?.ident

private fun ApplicationCall.sakstemakodeFromParameters(): Sakstema =
    parameters[sakstemakode]
        ?.let { resolveSakstemakode(it) }
        ?: throw InvalidRequestException("Kallet kan ikke utføres uten at '$sakstemakode' er spesifisert.")

private fun ApplicationCall.antallFromParameters(): Int? =
    parameters["antall"]
        ?.runCatching { toInt() }
        ?.getOrElse { throw InvalidRequestException("Ugyildig antall i parameter") }

private fun resolveSakstemakode(sakstemakode: String): Sakstema =
    try {
        Sakstema.valueOf(sakstemakode)
    } catch (cause: Exception) {
        throw InvalidRequestException("Ugyldig verdi for sakstemakode", cause)
    }


private fun ApplicationCall.enableRepr(): Boolean {
    return parameters["enable_repr"]
        ?.runCatching { toBoolean() }
        ?.getOrElse { throw InvalidRequestException("Feilaktig verdi for boolean-parameter 'enable_repr'") }
        ?: false
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
