package no.nav.tms.minesaker.api

import io.github.oshai.kotlinlogging.KLogger
import io.ktor.client.HttpClient
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
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import no.nav.tms.common.logging.TeamLogs
import no.nav.tms.common.metrics.installTmsMicrometerMetrics
import no.nav.tms.common.observability.ApiMdc
import no.nav.tms.minesaker.api.fullmakt.*
import no.nav.tms.minesaker.api.innsendte.DigiSosConsumer
import no.nav.tms.minesaker.api.innsendte.digiSosRoute
import no.nav.tms.minesaker.api.journalpost.SafService
import no.nav.tms.minesaker.api.journalpost.dokumentRoute
import no.nav.tms.minesaker.api.journalpost.journalpostRoutes
import no.nav.tms.minesaker.api.setup.*
import no.nav.tms.token.support.entraid.token.fetcher.EntraIdTokenFetcherBuilder
import no.nav.tms.token.support.user.login.routes.UserLoginRoutes
import no.nav.tms.token.support.user.token.verification.LevelOfAssurance
import no.nav.tms.token.support.user.token.verification.UserPrincipal
import no.nav.tms.token.support.user.token.verification.userToken

fun Application.mineSakerApi(
    safService: SafService,
    digiSosConsumer: DigiSosConsumer,
    httpClient: HttpClient,
    corsAllowedOrigins: String,
    fullmaktService: FullmaktService,
    fullmaktSessionStore: FullmaktSessionStore,
    authConfig: Application.() -> Unit
) {
    val log = KotlinLogging.logger { }
    val teamLog = TeamLogs.logger { }

    install(DefaultHeaders)

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            when (cause) {
                is InvalidRequestException -> {
                    call.respond(HttpStatusCode.BadRequest, cause.message!!)
                    log.warn { "Feil i request: ${cause.message}" }
                }

                is PrematureClientCloseException -> {
                    log.warn { cause.describe() }
                    teamLog.warn(cause) { cause.describe() }
                    call.respond(HttpStatusCode.NoContent)
                }

                is CommunicationException -> {
                    log.error { "Kommunikasjonsfeil mot SAF eller Digisos." }
                    teamLog.error(cause) { "Kommunikasjonsfeil mot SAF eller Digisos." }
                    call.respond(HttpStatusCode.ServiceUnavailable)
                }

                is SafResultException -> {
                    log.warn { "Feil i resultat fra SAF." }
                    teamLog.warn(cause) {
                        "Feil i graphql resultat for kall til ${call.request.uri}: \n${
                            cause.errors?.joinToString("\n") { it.message }
                        }"
                    }
                    resetFullmaktSession(call, fullmaktSessionStore, log, teamLog)
                    call.respond(HttpStatusCode.InternalServerError)
                }

                is DocumentNotFoundException -> {
                    log.warn { "Dokument ikke funnet." }
                    teamLog.warn(cause) { "Dokument { journalpostId: ${cause.journalpostId}, dokumentinfoId: ${cause.dokumentinfoId} } ikke funnet." }
                    call.respond(HttpStatusCode.NotFound)
                }

                is DocumentFormatNotAvailableException -> {
                    log.warn { "Dokument kan ikke vises i forespurt format." }
                    teamLog.warn(cause) { "Dokument { journalpostId: ${cause.journalpostId}, dokumentinfoId: ${cause.dokumentinfoId} } kunne ikke vises i variant [${cause.requestedVariant}]." }
                    call.respond(HttpStatusCode.Forbidden)
                }

                is UgyldigFullmaktException -> {
                    log.warn { "Bruker forsøkte å sette ugyldig fullmakt." }
                    teamLog.warn(cause) { "Bruker forsøkte å sette ugyldig fullmakt. Bruker ${cause.fullmektig} er ikke representant for ${cause.giver}" }

                    call.respond(HttpStatusCode.Forbidden)
                }

                else -> {
                    log.error { "Kall til ${call.request.uri} feiler" }
                    teamLog.error(cause) { "Kall til ${call.request.uri} feiler." }
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }
    }

    install(ApiMdc)

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
            fullmaktApi(fullmaktService, fullmaktSessionStore)
            dokumentRoute(safService)
            route("v2") {
                journalpostRoutes(safService)
            }
            route("ssr") {
                journalpostRoutes(safService)
            }
            route("debug") {
                val tokenFetcher = EntraIdTokenFetcherBuilder.buildFetcher()

                val client = HttpClientBuilder.build()

                get("test-1") {
                    val token = tokenFetcher.getAccessToken("dev-gcp.min-side.tms-azure-test-dummy")

                    val response = client.get("http://tms-azure-test-dummy/entraid/test/1") {
                        header(HttpHeaders.Authorization, "Bearer $token")
                    }

                    call.respond(response.status)
                }

                get("test-2") {
                    val token = tokenFetcher.getAccessToken("dev-gcp.min-side.tms-azure-test-dummy")

                    val response = client.get("http://tms-azure-test-dummy/entraid/test/2") {
                        header(HttpHeaders.Authorization, "Bearer $token")
                    }

                    call.respond(response.status)
                }
            }
        }
    }

    configureShutdownHook(httpClient)
}

private suspend fun resetFullmaktSession(
    call: ApplicationCall,
    fullmaktSessionStore: FullmaktSessionStore,
    log: KLogger,
    teamLog: KLogger
) = try {

    val ident = call.principal<UserPrincipal>()!!.ident

    fullmaktSessionStore.clearFullmaktGiver(ident)
} catch (e: Exception) {
    log.error { "Klarte ikke nullstille fullmakt-sesjon." }
    teamLog.error(e) { "Klarte ikke nullstille fullmakt-sesjon." }
}

val RoutingCall.user get() = principal<UserPrincipal>() ?: throw IllegalStateException("Fant ikke brukerprinsipal")

fun authConfig(): Application.() -> Unit = {
    install(UserLoginRoutes)

    authentication {
        userToken {
            levelOfAssurance = LevelOfAssurance.High
        }
    }
}

private fun Application.configureShutdownHook(httpClient: HttpClient) {
    monitor.subscribe(ApplicationStopping) {
        httpClient.close()
    }
}
