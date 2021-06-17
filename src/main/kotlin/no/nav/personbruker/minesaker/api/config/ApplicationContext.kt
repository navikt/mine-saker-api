package no.nav.personbruker.minesaker.api.config

import no.nav.personbruker.minesaker.api.health.HealthService
import no.nav.personbruker.minesaker.api.saf.SafConsumer
import no.nav.personbruker.minesaker.api.sak.SakService
import no.nav.personbruker.minesaker.api.saf.SafTokendings
import no.nav.tms.token.support.tokendings.exchange.TokendingsServiceBuilder

class ApplicationContext {

    val environment = Environment()

    val httpClient = HttpClientBuilder.build()
    val healthService = HealthService(this)

    val tokendingsService = TokendingsServiceBuilder.buildTokendingsService()

    val safTokendings = SafTokendings(tokendingsService, environment.safClientId)

    val safConsumer = SafConsumer(httpClient, safEndpoint = environment.safEndpoint)
    val sakService = SakService(safConsumer, safTokendings)
}
