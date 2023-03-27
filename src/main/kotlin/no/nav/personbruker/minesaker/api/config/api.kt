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
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import io.prometheus.client.hotspot.DefaultExports
import no.nav.personbruker.minesaker.api.common.exception.InvalidRequestException
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

    install(DefaultHeaders)
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            when (cause) {
                is InvalidRequestException -> {
                    call.respond(HttpStatusCode.BadRequest, cause.message ?: "Ukjen feil i request")
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
            enableMineSakerJsonConfig()
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
