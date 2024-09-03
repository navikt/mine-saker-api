package no.nav.tms.minesaker.api


import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import no.nav.tms.minesaker.api.setup.InvalidRequestException
import no.nav.tms.minesaker.api.saf.sakstemaer.Sakstemakode
import no.nav.tms.minesaker.api.saf.fullmakt.FullmaktAttribute
import no.nav.tms.minesaker.api.saf.fullmakt.enableFullmakt
import no.nav.tms.minesaker.api.saf.journalposter.v1.JournalposterResponse

const val sakstemakode = "sakstemakode"
const val dokumentIdParameterName = "dokumentId"
const val journalpostIdParameterName = "journalpostId"

fun Route.mineSakerRoute(service: SakService) {

    val log = KotlinLogging.logger { }
    val secureLog = KotlinLogging.logger("secureLog")

    enableFullmakt {
        get("/journalposter") {
            service.hentJournalposterForSakstema(
                user = idportenUser,
                sakstema = call.sakstemaFromQueryParameters(),
                representert = call.representert
            )?.let { result ->
                call.respond(HttpStatusCode.OK, listOf(result))
            }?: suspend {
                call.respond(HttpStatusCode.OK, emptyList<JournalposterResponse>())
            }
        }

        get("/journalposter/{$sakstemakode}") {
            service.hentJournalposterForSakstema(
                user = idportenUser,
                sakstema = call.sakstemakodeFromParameters(),
                representert = call.representert
            )?.let { result ->
                call.respond(HttpStatusCode.OK, listOf(result))
            }?: suspend {
                call.respond(HttpStatusCode.OK, emptyList<JournalposterResponse>())
            }
        }

        get("/sakstema/{$sakstemakode}/journalposter") {
            val sakstemakode =call.sakstemakodeFromParameters()

            service.hentJournalposterForSakstema(
                user = idportenUser,
                sakstema = sakstemakode,
                representert = call.representert
            )?.let { result ->
                call.respond(HttpStatusCode.OK, result)
            }?: suspend {
                call.respondText("Fant ikke journalposter med kode $sakstemakode",status = HttpStatusCode.NotFound)
            }
        }

        get("/sakstemaer") {
            val result = service.hentSakstemaer(
                user = idportenUser,
                representert = call.representert
            )
            if (result.hasErrors()) {
                log.warn { "En eller flere kilder i kall til /sakstemnaer feilet: ${result.errors()}" }
                secureLog.warn { "En eller flere kilder i kall til /sakstemaer for ident ${idportenUser.ident} feilet: ${result.errors()}" }
            }
            call.respond(result.determineHttpCode(), result.resultsSorted())
        }

        get("/v2/sakstema/{$sakstemakode}/journalposter") {
            val sakstemakode = call.sakstemakodeFromParameters()

            service.hentJournalposterV2(
                user = idportenUser,
                sakstema = sakstemakode,
                representert = call.representert
            )?.let { result ->
                call.respond(HttpStatusCode.OK, result)
            }?: suspend {
                call.respondText("Fant ikke journalposter med kode $sakstemakode",status = HttpStatusCode.NotFound)
            }
        }
    }

    get("/sakstema/{$sakstemakode}/journalpost/{$journalpostIdParameterName}") {
        val sakstemakode = call.sakstemakodeFromParameters()
        val journalpostId = call.journalpostId()

        val journalposter = service.hentJournalposterForSakstema(idportenUser, sakstemakode)

        journalposter?.journalposter
            ?.find { it.journalpostId == journalpostId }
            ?.let {
                call.respond(journalposter.copy(journalposter = listOf(it)))
            }?: suspend {
                call.respondText(
                    "Fant ikke journalpost med tema $sakstemakode og journalpostId $journalpostId",
                    status = HttpStatusCode.NotFound
                )
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

    get("debug/token") {
        call.respond(service.token(idportenUser))
    }
}

private suspend fun ByteWriteChannel.streamFrom(input: ByteReadChannel) {
    while (!input.isClosedForRead) {
        val packet = input.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
        while (!packet.isEmpty) {
            writePacket(packet)
        }
        flush()
    }
}

// Lenkes til eller kalles fra andre steder enn dokumentarkiv
fun Route.sakApiExternal(
    service: SakService,
    sakerUrl: String
) {
    val log = KotlinLogging.logger { }
    val secureLog = KotlinLogging.logger("secureLog")

    get("/sakstemaer/egne") {
        val result = service.hentSakstemaer(idportenUser)
        if (result.hasErrors()) {
            log.warn { "En eller flere kilder i kall til /sakstemnaer feilet: ${result.errors()}" }
            secureLog.warn { "En eller flere kilder i kall til /sakstemaer for ident ${idportenUser.ident} feilet: ${result.errors()}" }
        }
        call.respond(result.determineHttpCode(), result.resultsSorted())
    }

    get("/siste"){
        val result = service.hentSakstemaer(idportenUser)
        if (result.hasErrors()) {
            log.warn { "En eller flere kilder feilet: ${result.errors()}" }
        }
        call.respond(result.determineHttpCode(), result.recentlyModified(sakerUrl))
    }
}


private val ApplicationCall.representert get() =
    attributes.getOrNull(FullmaktAttribute)?.ident

private fun ApplicationCall.sakstemaFromQueryParameters() =
    request.queryParameters["sakstemakode"]
        ?.let { queryParam -> resolveSakstemakode(queryParam) }
        ?: throw InvalidRequestException("Parameter sakstemakode mangler")

private fun ApplicationCall.sakstemakodeFromParameters(): Sakstemakode =
    parameters[sakstemakode]
        ?.let { resolveSakstemakode(it) }
        ?: throw InvalidRequestException("Kallet kan ikke utføres uten at '$sakstemakode' er spesifisert.")

private fun resolveSakstemakode(sakstemakode: String): Sakstemakode =
    try {
        Sakstemakode.valueOf(sakstemakode)
    } catch (cause: Exception) {
        throw InvalidRequestException("Ugyldig verdi for sakstemakode", cause)
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
