package no.nav.personbruker.minesaker.api.sak


import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.personbruker.minesaker.api.common.ExceptionResponseHandler
import no.nav.personbruker.minesaker.api.common.exception.InvalidRequestException
import no.nav.personbruker.minesaker.api.config.idportenUser
import no.nav.personbruker.minesaker.api.domain.AuthenticatedUser
import no.nav.personbruker.minesaker.api.domain.Sakstemakode
import org.slf4j.LoggerFactory

const val sakstemakode = "sakstemakode"
const val dokumentIdParameterName = "dokumentId"
const val journalpostIdParameterName = "journalpostId"

val log = LoggerFactory.getLogger(SakService::class.java)

fun Route.sakApi(
    service: SakService
) {


    get("/journalposter") {
        val sakstema = call.sakstemaFromQueryParameters()
        val result = service.hentJournalposterForSakstema(idportenUser, sakstema)
        call.respond(HttpStatusCode.OK, result)
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
            val user = AuthenticatedUser.createIdPortenUser(idportenUser)
            val result = service.hentSakstemaer(user)
            if (result.hasErrors()) {
                log.warn("En eller flere kilder feilet: ${result.errors()}. Klienten får en passende http-svarkode.")
            }
            call.respond(result.determineHttpCode(), result.resultsSorted())

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


private fun ApplicationCall.sakstemaFromQueryParameters() =
    request.queryParameters["sakstemakode"]
        ?.let { queryParam -> resolveSakstemakode(queryParam) }
        ?: throw InvalidRequestException("Parameter sakstemakode mangler")


private fun resolveSakstemakode(sakstemakode: String): Sakstemakode =
    try {
        Sakstemakode.valueOf(sakstemakode)
    } catch (cause: Exception) {
        throw InvalidRequestException("Ugyldig verdi for sakstemakode", cause)
    }


private fun ApplicationCall.extractSakstemakodeFromParameters(): Sakstemakode {
    val sakstemakodeUverifisert = parameters[sakstemakode]
        ?: throw InvalidRequestException("Kallet kan ikke utføres uten at '$sakstemakode' er spesifisert.")

    return resolveSakstemakode(sakstemakodeUverifisert)
}

private fun ApplicationCall.extractJournalpostId(): String {
    return parameters[journalpostIdParameterName]
        ?: throw InvalidRequestException("Kallet kan ikke utføres uten at '$journalpostIdParameterName' er spesifisert.")
}

private fun ApplicationCall.extractDokumentInfoId(): String {
    return parameters[dokumentIdParameterName]
        ?: throw InvalidRequestException("Kallet kan ikke utføres uten at '$dokumentIdParameterName' er spesifisert.")
}
