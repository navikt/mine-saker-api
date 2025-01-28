package no.nav.tms.minesaker.api.innsendte

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.tms.minesaker.api.idportenUser

fun Route.digiSosRoute(digiSosConsumer: DigiSosConsumer) {

    val log = KotlinLogging.logger { }
    val secureLog = KotlinLogging.logger("secureLog")

    get("/v2/sosialhjelp/har_innsendte") {
        val harInnsendte = try {
            digiSosConsumer.harInnsendte(idportenUser)
        } catch (e: Exception) {
            log.error { "Klarte ikke å hente brukers data fra DigiSos." }
            secureLog.error(e) { "Klarte ikke å hente brukers (${idportenUser.ident}) data fra DigiSos." }
            false
        }

        call.respond(harInnsendte)
    }
}
