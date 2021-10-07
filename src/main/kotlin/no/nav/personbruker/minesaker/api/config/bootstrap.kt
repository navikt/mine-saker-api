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
import no.nav.personbruker.minesaker.api.debug.exchangeApi
import no.nav.personbruker.minesaker.api.health.healthApi
import no.nav.personbruker.minesaker.api.sak.dittNavSakApi
import no.nav.personbruker.minesaker.api.sak.sakApi
import no.nav.tms.token.support.authentication.installer.installAuthenticators
import no.nav.tms.token.support.idporten.SecurityLevel.LEVEL_4
import no.nav.tms.token.support.idporten.user.IdportenUserFactory
import no.nav.tms.token.support.tokenx.validation.TokenXAuthenticator
import no.nav.tms.token.support.tokenx.validation.user.TokenXUserFactory

@KtorExperimentalAPI
fun Application.mainModule(appContext: ApplicationContext = ApplicationContext()) {

    DefaultExports.initialize()

    install(DefaultHeaders)

    install(CORS) {
        host(appContext.environment.corsAllowedOrigins, schemes = listOf(appContext.environment.corsAllowedSchemes))
        allowCredentials = true
        header(HttpHeaders.ContentType)
    }

    installAuthenticators {
        installIdPortenAuth {
            setAsDefault = true
            postLogoutRedirectUri = appContext.environment.postLogoutUrl
            tokenCookieName = "mine_saker_api_token"
            securityLevel = LEVEL_4
            tokenRefreshEnabled = true
        }
        installTokenXAuth {
            setAsDefault = false
        }
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
            exchangeApi(appContext.safTokendings, appContext.digiSosTokendings, appContext.environment)
        }

        authenticate(TokenXAuthenticator.name) {
            dittNavSakApi(appContext.sakService)
        }

    }

    configureShutdownHook(appContext.httpClient)
}

private fun Application.configureShutdownHook(httpClient: HttpClient) {
    environment.monitor.subscribe(ApplicationStopping) {
        httpClient.close()
    }
}

val PipelineContext<*, ApplicationCall>.idportenUser get() = IdportenUserFactory.createIdportenUser(call)
val PipelineContext<*, ApplicationCall>.tokenXUser get() = TokenXUserFactory.createTokenXUser(call)
