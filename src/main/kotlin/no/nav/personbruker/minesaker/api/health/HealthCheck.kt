package no.nav.personbruker.minesaker.api.health

interface HealthCheck {

    suspend fun status(): HealthStatus

}
