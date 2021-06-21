package no.nav.personbruker.minesaker.api.sak

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.personbruker.minesaker.api.common.ExceptionResponseHandler
import no.nav.personbruker.minesaker.api.common.exception.InvalidRequestException
import no.nav.personbruker.minesaker.api.config.idportenUser
import no.nav.personbruker.minesaker.api.domain.DokumentInfoId
import no.nav.personbruker.minesaker.api.domain.JournalpostId
import no.nav.personbruker.minesaker.api.domain.Sakstemakode
import org.slf4j.LoggerFactory

val sakstemakode = "sakstemakode"
val dokumentIdParameterName = "dokumentId"
val journalpostIdParameterName = "journalpostId"

fun Route.sakApi(
    service: SakService
) {

    val log = LoggerFactory.getLogger(SakService::class.java)

    get("/journalposter") {
        try {
            val sakstema = call.extractSakstemaFromQueryParameters()
            val result = service.hentJournalposterForSakstema(idportenUser, sakstema)
            call.respond(HttpStatusCode.OK, result)

        } catch (exception: Exception) {
            val errorCode = ExceptionResponseHandler.logExceptionAndDecideErrorResponseCode(log, exception)
            call.respond(errorCode)
        }
    }

    get("/journalposter/{$sakstemakode}") {
        try {
            val sakstema = call.extractSakstemakodeFromParameters()
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
            if(result.hasErrors()) {
                log.warn("En eller flere kilder feilet: ${result.errors()}. Klienten får en passende http-svarkode.")
            }
            call.respond(result.determineHttpCode(), result.results())

        } catch (exception: Exception) {
            val errorCode = ExceptionResponseHandler.logExceptionAndDecideErrorResponseCode(log, exception)
            call.respond(errorCode)
        }
    }

    get("/saf") {
        try {
            val result = service.hentSakstemaerFraSaf(idportenUser)
            call.respond(HttpStatusCode.OK, result.results())

        } catch (exception: Exception) {
            val errorCode = ExceptionResponseHandler.logExceptionAndDecideErrorResponseCode(log, exception)
            call.respond(errorCode)
        }
    }

    get("/digisos") {
        try {
            val result = service.hentSakstemaerFraDigiSos(idportenUser)
            call.respond(HttpStatusCode.OK, result.results())

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

private fun ApplicationCall.extractSakstemaFromQueryParameters(): Sakstemakode {
    val sakstemakode: String = request.queryParameters["sakstemakode"]
        ?: throw InvalidRequestException("Kallet kan ikke utføres uten at tema er valgt.")

    return verifiserSakstemakode(sakstemakode)
}

private fun verifiserSakstemakode(sakstemakode: String): Sakstemakode {
    val sakstema = runCatching {
        Sakstemakode.valueOf(sakstemakode)

    }.onFailure { cause ->
        throw InvalidRequestException("Ugyldig sakstemakode ble brukt", cause)
    }

    return sakstema.getOrThrow()
}

private fun ApplicationCall.extractSakstemakodeFromParameters(): Sakstemakode {
    val sakstemakodeUverifisert = parameters[sakstemakode]
        ?: throw InvalidRequestException("Kallet kan ikke utføres uten at '$sakstemakode' er spesifisert.")

    return verifiserSakstemakode(sakstemakodeUverifisert)
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
