package no.nav.personbruker.minesaker.api.config

import no.nav.personbruker.minesaker.api.domain.Sakstemakode
import java.net.URL

class InnsynsUrlResolver(
    private val temaspesifikkeLenker: Map<Sakstemakode, URL>,
    private val generellLenke: URL
) {
    fun urlFor(kode: Sakstemakode): URL {
        return temaspesifikkeLenker.getOrDefault(kode, URL("$generellLenke$kode"))
    }
}

