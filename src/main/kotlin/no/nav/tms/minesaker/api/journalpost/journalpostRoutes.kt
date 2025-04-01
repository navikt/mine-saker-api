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
import no.nav.tms.minesaker.api.user

const val journalpostIdParameterName = "journalpostId"

fun Route.journalpostRoutes(service: SafService) {

    enableFullmakt {

        get("/journalposter/alle") {
            service.alleJournalposter(
                user = call.user,
                representert = call.representert
            ).let { result ->
                call.respond(HttpStatusCode.OK, result)
            }
        }

        get("/journalposter/journalpost/{$journalpostIdParameterName}") {
            val journalpostId = call.journalpostId()

            val representert = if (call.enableRepr()) {
                call.representert
            } else {
                null
            }

            service.hentJournalpost(
                user = call.user,
                journapostId = journalpostId,
                representert = representert
            )?.let { result ->
                call.respond(HttpStatusCode.OK, result)
            } ?: run {
                call.respondText("Fant ikke journalpost med id $journalpostId", status = HttpStatusCode.NotFound)
            }
        }
    }

    get("/journalposter/siste") {
        val antall = call.antallFromParameters() ?: 3

        call.respond(service.sisteJournalposter(call.user, antall))
    }
}

private val ApplicationCall.representert get() =
    attributes.getOrNull(FullmaktAttribute)?.ident

private fun ApplicationCall.antallFromParameters(): Int? =
    parameters["antall"]
        ?.runCatching { toInt() }
        ?.getOrElse { throw InvalidRequestException("Ugyildig antall i parameter") }

private fun ApplicationCall.enableRepr(): Boolean {
    return parameters["enable_repr"]
        ?.runCatching { toBoolean() }
        ?.getOrElse { throw InvalidRequestException("Feilaktig verdi for boolean-parameter 'enable_repr'") }
        ?: false
}

private fun ApplicationCall.journalpostId(): String = parameters[journalpostIdParameterName]
    ?: throw InvalidRequestException("Kallet kan ikke utføres uten at '$journalpostIdParameterName' er spesifisert.")
