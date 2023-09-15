package no.nav.personbruker.minesaker.api.config

import io.github.oshai.kotlinlogging.KLogger
import io.ktor.client.HttpClient
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationStopping
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import io.ktor.util.pipeline.PipelineContext
import io.prometheus.client.hotspot.DefaultExports
import io.github.oshai.kotlinlogging.KotlinLogging
import nav.no.tms.common.metrics.installTmsMicrometerMetrics
import no.nav.personbruker.minesaker.api.exception.CommunicationException
import no.nav.personbruker.minesaker.api.exception.DocumentNotFoundException
import no.nav.personbruker.minesaker.api.exception.GraphQLResultException
import no.nav.personbruker.minesaker.api.exception.InvalidRequestException
import no.nav.personbruker.minesaker.api.exception.TransformationException
import no.nav.personbruker.minesaker.api.health.healthApi
import no.nav.personbruker.minesaker.api.saf.fullmakt.*
import no.nav.personbruker.minesaker.api.sak.*
import no.nav.tms.token.support.idporten.sidecar.LevelOfAssurance
import no.nav.tms.token.support.idporten.sidecar.installIdPortenAuth
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUserFactory


fun Application.mineSakerApi(
    sakService: SakService,
    httpClient: HttpClient,
    corsAllowedOrigins: String,
    corsAllowedSchemes: String,
    sakerUrl: String,
    fullmaktService: FullmaktService,
    fullmaktSessionStore: FullmaktSessionStore,
    authConfig: Application.() -> Unit
) {
    DefaultExports.initialize()
    val log = KotlinLogging.logger { }
    val secureLog = KotlinLogging.logger("secureLogs")

    install(DefaultHeaders)
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            when (cause) {
                is InvalidRequestException -> {
                    call.respond(HttpStatusCode.BadRequest, cause.message ?: "Ukjent feil i request")
                    cause.sensitiveMessage?.let {
                        secureLog.error { it }
                    }
                }

                is CommunicationException -> {
                    log.error { "Kommunikasjonsfeil mot SAF eller Digisos." }
                    secureLog.warn(cause) { "Kommunikasjonsfeil mot SAF eller Digisos." }
                    call.respond(HttpStatusCode.ServiceUnavailable)
                }

                is GraphQLResultException -> {
                    log.warn { "Feil i resultat fra SAF." }
                    secureLog.warn(cause) {
                        "Feil i graphql resultat for kall til ${call.request.uri}: \n${
                            cause.errors?.joinToString("\n") { it.message }
                        }"
                    }
                    resetFullmaktSession(call, fullmaktSessionStore, log, secureLog)
                    call.respond(HttpStatusCode.InternalServerError)
                }

                is DocumentNotFoundException -> {
                    log.warn { "Dokument ikke funnet." }
                    secureLog.warn(cause) { "Dokument { journalpostId: ${cause.journalpostId}, dokumentinfoId: ${cause.dokumentinfoId} } ikke funnet." }
                    call.respond(HttpStatusCode.NotFound)
                }

                is TransformationException -> {
                    log.warn { "Feil ved transformering av data." }
                    secureLog.warn(cause) { "Feil ved transformering av data." }
                    call.respond(HttpStatusCode.InternalServerError)
                }

                is UgyldigFullmaktException -> {
                    log.warn { "Bruker forsøkte å sette ugyldig fullmakt." }
                    secureLog.warn(cause) { "Bruker forsøkte å sette ugyldig fullmakt. Bruker ${cause.fullmektig} er ikke representant for ${cause.giver}" }

                    call.respond(HttpStatusCode.Forbidden)
                }

                else -> {
                    log.error { "Kall til ${call.request.uri}" }
                    secureLog.error(cause) { "Kall til ${call.request.uri} feiler." }
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }
    }

    install(CORS) {
        allowHost(corsAllowedOrigins, schemes = listOf(corsAllowedSchemes))
        allowCredentials = true
        allowHeader(HttpHeaders.ContentType)
    }

    authConfig()
    install(FullmaktSessions) {
        sessionStore = fullmaktSessionStore
    }

    install(ContentNegotiation) {
        jackson {
            jsonConfig()
        }
    }

    installTmsMicrometerMetrics {
        setupMetricsRoute = true
        installMicrometerPlugin = true

        maskPathParams("/mine-saker-api/journalposter/{sakstemakode}")
        maskPathParams("/mine-saker-api/dokument/{journalpostId}/{dokumentId}")
        maskPathParams("/mine-saker-api/sakstema/{sakstemakode}/journalpost/{journalpostId}")
    }

    routing {
        healthApi()

        authenticate {
            sakApi(sakService)
            sakApiExternal(sakService, sakerUrl)
            fullmaktApi(fullmaktService, fullmaktSessionStore)
        }
    }

    configureShutdownHook(httpClient)
}

private suspend fun resetFullmaktSession(
    call: ApplicationCall,
    fullmaktSessionStore: FullmaktSessionStore,
    log: KLogger,
    secureLog: KLogger
) = try {
    val ident = IdportenUserFactory.createIdportenUser(call).ident

    fullmaktSessionStore.clearFullmaktGiver(ident)
} catch (e: Exception) {
    log.error { "Klarte ikke nullstille fullmakt-sesjon." }
    secureLog.error(e) { "Klarte ikke nullstille fullmakt-sesjon." }
}

fun authConfig(): Application.() -> Unit = {
    installIdPortenAuth {
        setAsDefault = true
        levelOfAssurance = LevelOfAssurance.HIGH
    }
}

private fun Application.configureShutdownHook(httpClient: HttpClient) {
    environment.monitor.subscribe(ApplicationStopping) {
        httpClient.close()
    }
}
val PipelineContext<*, ApplicationCall>.idportenUser get() = IdportenUserFactory.createIdportenUser(call)
