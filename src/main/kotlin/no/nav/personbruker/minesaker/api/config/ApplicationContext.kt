package no.nav.personbruker.minesaker.api.config

import no.nav.personbruker.minesaker.api.health.HealthService

class ApplicationContext {

    val httpClient = HttpClientBuilder.build()
    val healthService = HealthService(this)

}
