package no.nav.personbruker.minesaker.api.config

import no.nav.personbruker.minesaker.api.common.sak.SakService
import no.nav.personbruker.minesaker.api.health.HealthService
import no.nav.personbruker.minesaker.api.saf.SafConsumer

class ApplicationContext {

    val environment = Environment()

    val httpClient = HttpClientBuilder.build()
    val healthService = HealthService(this)

    val safConsumer = SafConsumer(httpClient, safEndpoint = environment.safEndpoint)
    val sakService = SakService(safConsumer)

}
