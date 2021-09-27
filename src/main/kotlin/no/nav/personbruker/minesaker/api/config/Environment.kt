package no.nav.personbruker.minesaker.api.config

import java.net.URL

data class Environment(
    val corsAllowedOrigins: String = getEnvVar("CORS_ALLOWED_ORIGINS"),
    val corsAllowedSchemes: String = getEnvVar("CORS_ALLOWED_SCHEMES", "https"),
    val safEndpoint: URL = URL(getEnvVar("SAF_API_URL")),
    val safClientId: String = getEnvVar("SAF_CLIENT_ID"),
    val digiSosEndpoint: URL = URL(getEnvVar("DIGISOS_API_URL")),
    val digiSosClientId: String = getEnvVar("DIGISOS_CLIENT_ID"),
    val clusterName: String = getEnvVar("NAIS_CLUSTER_NAME"),
    val postLogoutUrl: String = getEnvVar("POST_LOGOUT_URL")
)

fun getEnvVar(varName: String, default: String? = null): String {
    return System.getenv(varName)
            ?: default
            ?: throw IllegalArgumentException("Appen kan ikke starte uten av miljøvariabelen $varName er satt.")
}

fun Environment.isRunningInDev(): Boolean {
    return !isRunningInProd()
}

fun Environment.isRunningInProd(): Boolean {
    return "prod-gcp" == clusterName
}
