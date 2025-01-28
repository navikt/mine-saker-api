package no.nav.tms.minesaker.api

import io.github.oshai.kotlinlogging.KLogger
import io.ktor.client.HttpClient
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationStopping
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import no.nav.tms.common.metrics.installTmsMicrometerMetrics
import no.nav.tms.token.support.idporten.sidecar.IdPortenLogin
import no.nav.tms.token.support.idporten.sidecar.LevelOfAssurance
import no.nav.tms.token.support.idporten.sidecar.idPorten
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUserFactory
import no.nav.tms.common.observability.ApiMdc
import no.nav.tms.minesaker.api.fullmakt.*
import no.nav.tms.minesaker.api.innsendte.DigiSosConsumer
import no.nav.tms.minesaker.api.innsendte.digiSosRoute
import no.nav.tms.minesaker.api.journalpost.SafService
import no.nav.tms.minesaker.api.journalpost.journalpostRouteExternal
import no.nav.tms.minesaker.api.journalpost.journalpostRoutes
import no.nav.tms.minesaker.api.setup.*


fun Application.mineSakerApi(
    sakService: SafService,
    digiSosConsumer: DigiSosConsumer,
    httpClient: HttpClient,
    corsAllowedOrigins: String,
    fullmaktService: FullmaktService,
    fullmaktSessionStore: FullmaktSessionStore,
    authConfig: Application.() -> Unit
) {
    val log = KotlinLogging.logger { }
    val secureLog = KotlinLogging.logger("secureLog")

    install(DefaultHeaders)
    install(ApiMdc)

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
                    secureLog.error(cause) { "Kommunikasjonsfeil mot SAF eller Digisos." }
                    call.respond(HttpStatusCode.ServiceUnavailable)
                }

                is SafResultException -> {
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
        allowHost(corsAllowedOrigins)
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
    }

    routing {
        healthApi()

        authenticate {
            digiSosRoute(digiSosConsumer)
            journalpostRoutes(sakService)
            fullmaktApi(fullmaktService, fullmaktSessionStore)
        }

        authenticate(SubstantialAuth) {
            journalpostRouteExternal(sakService)
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
    install(IdPortenLogin)

    authentication {
        idPorten {
            setAsDefault = true
            levelOfAssurance = LevelOfAssurance.HIGH
        }

        idPorten {
            authenticatorName = SubstantialAuth
            setAsDefault = false
            levelOfAssurance = LevelOfAssurance.SUBSTANTIAL
        }
    }
}

const val SubstantialAuth = "substantial_auth"

private fun Application.configureShutdownHook(httpClient: HttpClient) {
    monitor.subscribe(ApplicationStopping) {
        httpClient.close()
    }
}
val RoutingContext.idportenUser get() = IdportenUserFactory.createIdportenUser(call)
