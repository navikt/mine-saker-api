package no.nav.personbruker.minesaker.api.config

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.client.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import io.prometheus.client.hotspot.DefaultExports
import no.nav.personbruker.minesaker.api.common.AuthenticatedUser
import no.nav.personbruker.minesaker.api.common.AuthenticatedUserFactory
import no.nav.personbruker.minesaker.api.common.sak.sakApi
import no.nav.personbruker.minesaker.api.health.healthApi
import no.nav.security.token.support.ktor.tokenValidationSupport

@KtorExperimentalAPI
fun Application.mainModule(appContext: ApplicationContext = ApplicationContext()) {

    DefaultExports.initialize()

    install(DefaultHeaders)

    install(CORS) {
        host(appContext.environment.corsAllowedOrigins)
        allowCredentials = true
        header(HttpHeaders.ContentType)
    }

    val config = this.environment.config

    install(Authentication) {
        tokenValidationSupport(config = config)
    }

    install(ContentNegotiation) {
        jackson {
            enableMineSakerJsonConfig()
        }
    }

    routing {
        healthApi(appContext.healthService)

        authenticate {
            sakApi(appContext.sakService)
        }

        configureShutdownHook(appContext.httpClient)
    }
}

private fun Application.configureShutdownHook(httpClient: HttpClient) {
    environment.monitor.subscribe(ApplicationStopping) {
        httpClient.close()
    }
}

val PipelineContext<Unit, ApplicationCall>.authenticatedUser: AuthenticatedUser
    get() = AuthenticatedUserFactory.createNewAuthenticatedUser(call)
