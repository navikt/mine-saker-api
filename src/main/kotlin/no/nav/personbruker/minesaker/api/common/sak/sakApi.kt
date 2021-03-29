package no.nav.personbruker.minesaker.api.common.sak

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.personbruker.minesaker.api.common.ExceptionResponseHandler
import no.nav.personbruker.minesaker.api.common.exception.InvalidRequestException
import no.nav.personbruker.minesaker.api.config.idportenUser
import no.nav.personbruker.minesaker.api.saf.domain.DokumentInfoId
import no.nav.personbruker.minesaker.api.saf.domain.JournalpostId
import no.nav.personbruker.minesaker.api.saf.domain.Sakstemakode
import no.nav.personbruker.minesaker.api.saf.domain.toInternalSaktemakode
import org.slf4j.LoggerFactory

val dokumentIdParameterName = "dokumentId"
val journalpostIdParameterName = "journalpostId"

fun Route.sakApi(
    service: SakService
) {

    val log = LoggerFactory.getLogger(SakService::class.java)

    get("/journalposter") {
        try {
            val sakstema = call.extractOnsketSakstema()
            val result = service.hentJournalposterForSakstema(idportenUser, sakstema)
            call.respond(HttpStatusCode.OK, result)

        } catch (exception: Exception) {
            val errorCode = ExceptionResponseHandler.logExceptionAndDecideErrorResponseCode(log, exception)
            call.respond(errorCode)
        }
    }

    get("/sakstemaer") {
        try {
            val result = service.hentSakstemaer(idportenUser)
            call.respond(HttpStatusCode.OK, result)

        } catch (exception: Exception) {
            val errorCode = ExceptionResponseHandler.logExceptionAndDecideErrorResponseCode(log, exception)
            call.respond(errorCode)
        }
    }

    get("/dokument/{$journalpostIdParameterName}/{$dokumentIdParameterName}") {
        try {
            val journalpostId = call.extractJournalpostId()
            val dokumentId = call.extractDokumentInfoId()
            log.info("Skal hente dokumentet $dokumentId, fra journalposten $journalpostId")
            val result = service.hentDokument(idportenUser, journalpostId, dokumentId)
            call.respondBytes(bytes = result, contentType = ContentType.Application.Pdf, status = HttpStatusCode.OK)

        } catch (exception: Exception) {
            val errorCode = ExceptionResponseHandler.logExceptionAndDecideErrorResponseCode(log, exception)
            call.respond(errorCode)
        }
    }

}

private fun ApplicationCall.extractOnsketSakstema(): Sakstemakode {
    val sakstemakode: String = request.queryParameters["sakstemakode"]
        ?: throw InvalidRequestException("Kallet kan ikke utføres uten at tema er valgt.")

    val sakstema = runCatching {
        sakstemakode.toInternalSaktemakode()

    }.onFailure { cause ->
        throw InvalidRequestException("Ugyldig sakstemakode ble brukt", cause)
    }

    return sakstema.getOrThrow()
}

private fun ApplicationCall.extractJournalpostId(): JournalpostId {
    val value = parameters[journalpostIdParameterName]
        ?: throw InvalidRequestException("Kallet kan ikke utføres uten at '$journalpostIdParameterName' er spesifisert.")
    return JournalpostId(value)
}

private fun ApplicationCall.extractDokumentInfoId(): DokumentInfoId {
    val value = parameters[dokumentIdParameterName]
        ?: throw InvalidRequestException("Kallet kan ikke utføres uten at '$dokumentIdParameterName' er spesifisert.")
    return DokumentInfoId(value)
}
