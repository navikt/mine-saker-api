package no.nav.personbruker.minesaker.api.config

import no.nav.personbruker.minesaker.api.domain.Sakstemakode
import java.net.URL

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
        return temaspesifikkeLenker.getOrDefault(kode, generellLenke)
    }

}
