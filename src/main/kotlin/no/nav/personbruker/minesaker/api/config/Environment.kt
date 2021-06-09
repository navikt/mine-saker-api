package no.nav.personbruker.minesaker.api.config

import java.net.URL

data class Environment(
    val corsAllowedOrigins: String = getEnvVar("CORS_ALLOWED_ORIGINS"),
    val corsAllowedSchemes: String = getEnvVar("CORS_ALLOWED_SCHEMES", "https"),
    val safEndpoint: URL = URL(getEnvVar("SAF_API_URL")),
    val safClientId: String = getEnvVar("SAF_CLIENT_ID"),
    val clusterName: String = getEnvVar("NAIS_CLUSTER_NAME"),
    val postLogoutUrl: String = getEnvVar("POST_LOGOUT_URL"),
    val sakApiUrl: String = getEnvVar("SAK_API_URL")
)

fun getEnvVar(varName: String, default: String? = null): String {
    return System.getenv(varName)
            ?: default
            ?: throw IllegalArgumentException("Appen kan ikke starte uten av miljøvariabelen $varName er satt.")
}
