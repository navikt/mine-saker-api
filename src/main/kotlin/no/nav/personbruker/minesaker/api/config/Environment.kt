package no.nav.personbruker.minesaker.api.config

data class Environment(
    val corsAllowedOrigins: String = getEnvVar("CORS_ALLOWED_ORIGINS")
)

fun getEnvVar(varName: String): String {
    return System.getenv(varName)
        ?: throw IllegalArgumentException("Appen kan ikke starte uten av miljøvariabelen $varName er satt.")
}
