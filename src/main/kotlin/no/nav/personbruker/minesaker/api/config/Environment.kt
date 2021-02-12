package no.nav.personbruker.minesaker.api.config

import java.net.URL

data class Environment(
    val corsAllowedOrigins: String = getEnvVar("CORS_ALLOWED_ORIGINS"),
    val safEndpoint: URL = URL(getEnvVar("SAF_API_URL"))
)

fun getEnvVar(varName: String): String {
    return System.getenv(varName)
        ?: throw IllegalArgumentException("Appen kan ikke starte uten av miljøvariabelen $varName er satt.")
}
