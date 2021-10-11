package no.nav.personbruker.minesaker.api.config

import no.nav.personbruker.minesaker.api.domain.Sakstemakode
import org.slf4j.LoggerFactory
import java.net.URL

private val log = LoggerFactory.getLogger(InnsynsUrlResolver::class.java)

val innsynsUrlResolverSingleton = if (NaisEnvironment.isRunningInProd()) {
    log.info("InnsynsUrlResolver konfigurert for PROD")
    InnsynsUrlResolver(true)

} else {
    log.info("InnsynsUrlResolver konfigurert for DEV")
    InnsynsUrlResolver(false)
}

class InnsynsUrlResolver(
    isRunningInProd : Boolean
) {

    private val temaspesifikkeLenker : Map<Sakstemakode, URL>
    private val generellLenke : URL

    init {
        if(isRunningInProd) {
            temaspesifikkeLenker = innsynslenkerProd
            generellLenke = generellInnsynslenkeProd

        } else {
            temaspesifikkeLenker = innsynslenkerDev
            generellLenke = generellInnsynslenkeDev
        }
    }

    fun urlFor(kode : Sakstemakode) : URL {
        return temaspesifikkeLenker.getOrDefault(kode, URL("$generellLenke$kode"))
    }

}
