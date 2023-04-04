package no.nav.personbruker.minesaker.api.config

import no.nav.personbruker.minesaker.api.domain.Sakstemakode

class InnsynsUrlResolver(
    private val temaspesifikkeLenker: Map<Sakstemakode, String>,
    private val generellLenke: String
) {
    fun urlFor(kode: Sakstemakode): String =
        temaspesifikkeLenker.getOrDefault(kode, "$generellLenke/$kode")

}

