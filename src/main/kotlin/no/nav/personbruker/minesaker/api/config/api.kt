package no.nav.personbruker.minesaker.api.config

import io.ktor.client.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import io.prometheus.client.hotspot.DefaultExports
import mu.KotlinLogging
import no.nav.personbruker.minesaker.api.exception.CommunicationException
import no.nav.personbruker.minesaker.api.exception.DocumentNotFoundException
import no.nav.personbruker.minesaker.api.exception.GraphQLResultException
import no.nav.personbruker.minesaker.api.exception.InvalidRequestException
import no.nav.personbruker.minesaker.api.exception.TransformationException
import no.nav.personbruker.minesaker.api.health.healthApi
import no.nav.personbruker.minesaker.api.sak.SakService
import no.nav.personbruker.minesaker.api.sak.dittNavSakApi
import no.nav.personbruker.minesaker.api.sak.sakApi
import no.nav.tms.token.support.authentication.installer.installAuthenticators
import no.nav.tms.token.support.idporten.sidecar.LoginLevel
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUserFactory
import no.nav.tms.token.support.tokenx.validation.TokenXAuthenticator
import no.nav.tms.token.support.tokenx.validation.user.TokenXUserFactory


fun Application.mineSakerApi(
    sakService: SakService,
    httpClient: HttpClient,
    corsAllowedOrigins: String,
    corsAllowedSchemes: String,
    rootPath: String,
    authConfig: Application.() -> Unit,

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
                sakApi(sakService)
            }

            authenticate(TokenXAuthenticator.name) {
                dittNavSakApi(sakService)
            }
        }
    }

    configureShutdownHook(httpClient)
}

fun authConfig(contextPath: String): Application.() -> Unit = {
    installAuthenticators {
        installIdPortenAuth {
            setAsDefault = true
            loginLevel = LoginLevel.LEVEL_4
            inheritProjectRootPath = false
            rootPath = contextPath
        }
        installTokenXAuth {
            setAsDefault = false
        }
    }
}

private fun Application.configureShutdownHook(httpClient: HttpClient) {
    environment.monitor.subscribe(ApplicationStopping) {
        httpClient.close()
    }
}

val PipelineContext<*, ApplicationCall>.idportenUser get() = IdportenUserFactory.createIdportenUser(call)

val PipelineContext<*, ApplicationCall>.tokenXUser
    get() = TokenXUserFactory.createTokenXUser(call)
