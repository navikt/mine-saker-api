package no.nav.personbruker.minesaker.api.config

import io.ktor.client.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import io.prometheus.client.hotspot.DefaultExports
import no.nav.personbruker.minesaker.api.health.healthApi
import no.nav.personbruker.minesaker.api.sak.dittNavSakApi
import no.nav.personbruker.minesaker.api.sak.sakApi
import no.nav.tms.token.support.authentication.installer.installAuthenticators
import no.nav.tms.token.support.idporten.sidecar.LoginLevel.LEVEL_4
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUserFactory
import no.nav.tms.token.support.tokenx.validation.TokenXAuthenticator
import no.nav.tms.token.support.tokenx.validation.user.TokenXUserFactory

fun main() {
    val appContext = ApplicationContext()

    embeddedServer(Netty, port = appContext.environment.port) {
        mineSakerApi(appContext, authConfig(appContext.environment.rootPath))
    }.start(wait = true)
}

fun Application.mineSakerApi(
    appContext: ApplicationContext,
    authConfig: Application.() -> Unit
) {
    DefaultExports.initialize()

    install(DefaultHeaders)

    install(CORS) {
        allowHost(appContext.environment.corsAllowedOrigins, schemes = listOf(appContext.environment.corsAllowedSchemes))
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
        route("/${appContext.environment.rootPath}") {
            healthApi()

            authenticate {
                sakApi(appContext.sakService)
            }

            authenticate(TokenXAuthenticator.name) {
                dittNavSakApi(appContext.sakService)
            }
        }
    }

    configureShutdownHook(appContext.httpClient)
}

fun authConfig(contextPath: String): Application.() -> Unit = {
    installAuthenticators {
        installIdPortenAuth {
            setAsDefault = true
            loginLevel = LEVEL_4
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
