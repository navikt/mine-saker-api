package no.nav.tms.minesaker.api.saf.journalposter

import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import no.nav.dokument.saf.selvbetjening.generated.dto.enums.Tema
import org.junit.jupiter.api.Test

internal class SakstemaTest {

    private val log = KotlinLogging.logger { }

    @Test
    fun `Skal inneholde alle kjente temakoder fra SAF`() {
        val externalTemaer = Tema.entries
        val internalSakstemaerForSAF = Sakstema.entries

        val manglendeTemaer = mutableListOf<String>()
        externalTemaer.forEach { external ->

            runCatching {
                if (external.isTemaSomSkalRepresenteresInternt()) {
                    Sakstema.valueOf(external.toString())
                }

            }.onFailure {
                manglendeTemaer.add(external.toString())
            }
        }

        if (manglendeTemaer.isNotEmpty()) {
            log.info { "manglendeTemaer: $manglendeTemaer" }
        }
        manglendeTemaer.shouldBeEmpty()
    }

    private fun Tema.isTemaSomSkalRepresenteresInternt() =
        this != Tema.__UNKNOWN_VALUE

    @Test
    fun `Skal inneholde sakstemakoden for DigiSos`() {
        val kodeForDigiSos = Sakstema.valueOf("KOM")
        kodeForDigiSos.shouldNotBeNull()
    }

}
