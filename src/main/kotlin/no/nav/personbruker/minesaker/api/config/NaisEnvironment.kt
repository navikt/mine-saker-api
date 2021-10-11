package no.nav.personbruker.minesaker.api.config

object NaisEnvironment {

    private fun currentClusterName(): String? = System.getenv("NAIS_CLUSTER_NAME")

    fun isRunningInProd(): Boolean {
        val clusterName = currentClusterName()
        return clusterName == "prod-gcp"
    }

    fun isRunningInDev(): Boolean {
        return !isRunningInProd()
    }

}
