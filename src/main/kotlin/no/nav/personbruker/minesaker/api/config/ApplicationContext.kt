package no.nav.personbruker.minesaker.api.config

import no.nav.personbruker.minesaker.api.common.sak.SakService
import no.nav.personbruker.minesaker.api.health.HealthService
import no.nav.personbruker.minesaker.api.saf.SafConsumer
import no.nav.personbruker.minesaker.api.tokenx.TokendingsServiceWrapper
import no.nav.tms.token.support.tokendings.exchange.TokendingsServiceBuilder

class ApplicationContext {

    val environment = Environment()

    val httpClient = HttpClientBuilder.build()
    val healthService = HealthService(this)

    val tokendingsService = TokendingsServiceBuilder.buildTokendingsService()

    val tokendingsServiceWrapper = TokendingsServiceWrapper(tokendingsService, environment.safClientId)

    val safConsumer = SafConsumer(httpClient, safEndpoint = environment.safEndpoint)
    val sakService = SakService(safConsumer, tokendingsServiceWrapper)
}
