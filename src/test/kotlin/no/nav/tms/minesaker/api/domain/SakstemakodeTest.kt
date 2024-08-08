package no.nav.tms.minesaker.api.domain

import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import no.nav.tms.minesaker.api.saf.journalposter.v1.SafTema
import no.nav.tms.minesaker.api.saf.sakstemaer.Sakstemakode
import org.junit.jupiter.api.Test

internal class SakstemakodeTest {

    private val log = KotlinLogging.logger { }

    @Test
    fun `Skal inneholde alle kjente temakoder fra SAF`() {
        val externalTemaer = SafTema.values()
        val internalSakstemaerForSAF = Sakstemakode.teamKoderFraSAF()

        val manglendeTemaer = mutableListOf<String>()
        externalTemaer.forEach { external ->

            runCatching {
                if (external.isTemaSomSkalRepresenteresInternt()) {
                    Sakstemakode.valueOf(external.toString())
                }

            }.onFailure {
                manglendeTemaer.add(external.toString())
            }
        }

        if (manglendeTemaer.isNotEmpty()) {
            log.info { "manglendeTemaer: $manglendeTemaer" }
        }
        manglendeTemaer.shouldBeEmpty()

        externalTemaer.size - 1 shouldBe internalSakstemaerForSAF.size
    }

    private fun SafTema.isTemaSomSkalRepresenteresInternt() =
        this != SafTema.__UNKNOWN_VALUE

    @Test
    fun `Skal inneholde sakstemakoden for DigiSos`() {
        val kodeForDigiSos = Sakstemakode.valueOf("KOM")
        kodeForDigiSos.shouldNotBeNull()
    }

}
