package no.nav.tms.minesaker.api.saf

import no.nav.tms.minesaker.api.saf.sakstemaer.Sakstemakode

class InnsynsUrlResolver(
    private val temaspesifikkeLenker: Map<Sakstemakode, String>,
    private val generellLenke: String
) {
    fun urlFor(kode: Sakstemakode): String =
        temaspesifikkeLenker.getOrDefault(kode, "$generellLenke/$kode")
}
