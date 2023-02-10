package no.nav.personbruker.minesaker.api.config

import no.nav.personbruker.dittnav.common.util.config.IntEnvVar.getEnvVarAsInt
import no.nav.personbruker.dittnav.common.util.config.StringEnvVar.getEnvVar
import java.net.URL

data class Environment(
    val rootPath: String = getEnvVar("ROOT_PATH", "mine-saker-api"),
    val port: Int = getEnvVarAsInt("PORT", 8080),
    val corsAllowedOrigins: String = getEnvVar("CORS_ALLOWED_ORIGINS"),
    val corsAllowedSchemes: String = getEnvVar("CORS_ALLOWED_SCHEMES", "https"),
    val safEndpoint: URL = URL(getEnvVar("SAF_API_URL")),
    val safClientId: String = getEnvVar("SAF_CLIENT_ID"),
    val digiSosEndpoint: URL = URL(getEnvVar("DIGISOS_API_URL")),
    val digiSosClientId: String = getEnvVar("DIGISOS_CLIENT_ID"),
    val clusterName: String = getEnvVar("NAIS_CLUSTER_NAME"),
    val postLogoutUrl: String = getEnvVar("POST_LOGOUT_URL")
)

fun Environment.isRunningInDev(): Boolean {
    return !isRunningInProd()
}

fun Environment.isRunningInProd(): Boolean {
    return "prod-gcp" == clusterName
}
