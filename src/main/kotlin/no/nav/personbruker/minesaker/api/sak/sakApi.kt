package no.nav.personbruker.minesaker.api.sak


import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import no.nav.personbruker.minesaker.api.common.exception.InvalidRequestException
import no.nav.personbruker.minesaker.api.config.idportenUser
import no.nav.personbruker.minesaker.api.domain.AuthenticatedUser
import no.nav.personbruker.minesaker.api.domain.Sakstemakode

const val sakstemakode = "sakstemakode"
const val dokumentIdParameterName = "dokumentId"
const val journalpostIdParameterName = "journalpostId"

fun Route.sakApi(
    service: SakService
) {

    val log = KotlinLogging.logger { }
    val secureLog = KotlinLogging.logger("secureLog")


    get("/journalposter") {
        val sakstema = call.sakstemaFromQueryParameters()
        val result = service.hentJournalposterForSakstema(idportenUser, sakstema)
        call.respond(HttpStatusCode.OK, result)
    }

    get("/journalposter/{$sakstemakode}") {
        val sakstema = call.sakstemakodeFromParameters()
        val result = service.hentJournalposterForSakstema(idportenUser, sakstema)
        call.respond(HttpStatusCode.OK, result)
    }

    get("/sakstemaer") {
        val user = AuthenticatedUser.createIdPortenUser(idportenUser)
        val result = service.hentSakstemaer(user)
        if (result.hasErrors()) {
            log.warn { "En eller flere kilder i kall til /sakstemnaer feilet: ${result.errors()}" }
            secureLog.warn { "En eller flere kilder i kall til /sakstemner for ident ${idportenUser.ident} feilet: ${result.errors()}" }
        }
        call.respond(result.determineHttpCode(), result.resultsSorted())
    }

    get("/dokument/{$journalpostIdParameterName}/{$dokumentIdParameterName}") {
        val journalpostId = call.journalpostId()
        val dokumentId = call.dokumentInfoId()
        log.info("Skal hente dokumentet $dokumentId, fra journalposten $journalpostId")
        val result = service.hentDokument(idportenUser, journalpostId, dokumentId)
        call.respondBytes(bytes = result, contentType = ContentType.Application.Pdf, status = HttpStatusCode.OK)
    }
}


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

