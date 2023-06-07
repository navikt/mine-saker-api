package no.nav.personbruker.minesaker.api.config

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
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.util.pipeline.PipelineContext
import io.prometheus.client.hotspot.DefaultExports
import mu.KotlinLogging
import no.nav.personbruker.minesaker.api.exception.CommunicationException
import no.nav.personbruker.minesaker.api.exception.DocumentNotFoundException
import no.nav.personbruker.minesaker.api.exception.GraphQLResultException
import no.nav.personbruker.minesaker.api.exception.InvalidRequestException
import no.nav.personbruker.minesaker.api.exception.TransformationException
import no.nav.personbruker.minesaker.api.health.healthApi
import no.nav.personbruker.minesaker.api.sak.SakService
import no.nav.personbruker.minesaker.api.sak.sakApi
import no.nav.tms.token.support.idporten.sidecar.LoginLevel
import no.nav.tms.token.support.idporten.sidecar.installIdPortenAuth
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUserFactory


fun Application.mineSakerApi(
    sakService: SakService,
    httpClient: HttpClient,
    corsAllowedOrigins: String,
    corsAllowedSchemes: String,
    rootPath: String,
    sakerUrl: String,
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
                    log.error { cause.message }
                    cause.sensitiveMessage?.let {
                        secureLog.error { it }
                    }
                    secureLog.warn { cause.stackTrace }
                    call.respond(HttpStatusCode.ServiceUnavailable)
                }

                is GraphQLResultException -> {
                    log.warn { cause.message }
                    secureLog.warn {
                        "Feil i graphql resultat for kall til ${call.request.uri}: \n${
                            cause.errors?.joinToString("\n") { it.message }
                        }"
                    }
                    secureLog.warn { cause.stackTrace }
                    call.respond(HttpStatusCode.InternalServerError)
                }

                is DocumentNotFoundException -> {
                    log.warn { cause.message }
                    cause.sensitiveMessage?.let {
                        secureLog.warn { "$it" }
                    }
                    call.respond(HttpStatusCode.NotFound)
                }

                is TransformationException -> {
                    log.warn { cause.message }
                    secureLog.warn { cause.stackTrace }
                    call.respond(HttpStatusCode.InternalServerError)
                }

                else -> {
                    secureLog.error { "Kall til ${call.request.uri} feiler: ${cause.message}" }
                    secureLog.warn { cause.stackTrace }
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

    install(ContentNegotiation) {
        jackson {
            jsonConfig()
        }
    }

    routing {
        route("/${rootPath}") {
            healthApi()

            authenticate {
                sakApi(sakService, sakerUrl)
            }
        }
    }

    configureShutdownHook(httpClient)
}

fun authConfig(contextPath: String): Application.() -> Unit = {
    installIdPortenAuth {
        setAsDefault = true
        loginLevel = LoginLevel.LEVEL_4
        inheritProjectRootPath = false
        rootPath = contextPath
    }
}

private fun Application.configureShutdownHook(httpClient: HttpClient) {
    environment.monitor.subscribe(ApplicationStopping) {
        httpClient.close()
    }
}
val PipelineContext<*, ApplicationCall>.idportenUser get() = IdportenUserFactory.createIdportenUser(call)