package no.nav.personbruker.minesaker.api.config

import org.slf4j.LoggerFactory

object NaisEnvironment {

    private val log = LoggerFactory.getLogger(NaisEnvironment::class.java)

    private fun currentClusterName(): String? = System.getenv("NAIS_CLUSTER_NAME")

    fun isRunningInProd(): Boolean {
        val clusterName = currentClusterName()
        log.info("Current cluster: $clusterName")
        return clusterName === "prod-gcp"
    }

    fun isRunningInDev(): Boolean {
        return !isRunningInProd()
    }

}
