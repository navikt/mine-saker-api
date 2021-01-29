package no.nav.personbruker.minesaker.api.health

import no.nav.personbruker.minesaker.api.config.ApplicationContext

class HealthService(private val applicationContext: ApplicationContext) {

    suspend fun getHealthChecks(): List<HealthStatus> {
        return emptyList()
    }
}
