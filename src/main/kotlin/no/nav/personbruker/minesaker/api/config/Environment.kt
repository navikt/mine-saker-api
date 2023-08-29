package no.nav.personbruker.minesaker.api.config

import no.nav.personbruker.dittnav.common.util.config.IntEnvVar.getEnvVarAsInt
import no.nav.personbruker.dittnav.common.util.config.StringEnvVar.getEnvVar
import no.nav.personbruker.minesaker.api.domain.Sakstemakode
import java.net.URL

data class Environment(
    val port: Int = getEnvVarAsInt("PORT", 8080),
    val corsAllowedOrigins: String = getEnvVar("CORS_ALLOWED_ORIGINS"),
    val corsAllowedSchemes: String = getEnvVar("CORS_ALLOWED_SCHEMES", "https"),
    val safEndpoint: URL = URL(getEnvVar("SAF_API_URL")),
    val safClientId: String = getEnvVar("SAF_CLIENT_ID"),
    val digiSosEndpoint: URL = URL(getEnvVar("DIGISOS_API_URL")),
    val digiSosClientId: String = getEnvVar("DIGISOS_CLIENT_ID"),
    val clusterName: String = getEnvVar("NAIS_CLUSTER_NAME"),
    val sakerUrl: String = getEnvVar("MINE_SAKER_URL"),
    val defaultInnsynLenke: String = getEnvVar("DEFAULT_INNSYN_LENKE"),
    val innsynsLenker: Map<Sakstemakode, String> = mapOf(
        Sakstemakode.KOM to getEnvVar("SOSIALHJELP_INNSYN")
    ),
    val pdlFullmaktUrl: String = getEnvVar("PDL_FULLMAKT_URL"),
    val pdlFullmaktClientId: String = getEnvVar("PDL_FULLMAKT_CLIENT_ID"),
    val pdlApiUrl: String = getEnvVar("PDL_API_URL"),
    val pdlApiClientId: String = getEnvVar("PDL_API_CLIENT_ID"),
)

