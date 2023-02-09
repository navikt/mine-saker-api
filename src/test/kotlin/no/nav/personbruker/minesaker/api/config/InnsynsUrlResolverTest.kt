package no.nav.personbruker.minesaker.api.config

import io.kotest.matchers.shouldBe
import no.nav.personbruker.minesaker.api.domain.Sakstemakode
import org.junit.jupiter.api.Test

internal class InnsynsUrlResolverTest {

    @Test
    fun `Skal returnere prod-lenker for alle sakstemaer`() {
        val resolver = InnsynsUrlResolver(true)
        val temaerMedSpesifikkeLenkerIProd = innsynslenkerProd.keys
        val sakstemakoderMedGenerellLenkeIProd = Sakstemakode.values().toMutableSet()
            .minus(temaerMedSpesifikkeLenkerIProd)

        temaerMedSpesifikkeLenkerIProd.forEach { tema ->
            resolver.urlFor(tema) shouldBe innsynslenkerProd[tema]
        }

        sakstemakoderMedGenerellLenkeIProd.forEach { tema ->
            resolver.urlFor(tema).toString() shouldBe "$generellInnsynslenkeProd$tema"
        }
    }

    @Test
    fun `Skal returnere dev-lenker for alle sakstemaer`() {
        val resolver = InnsynsUrlResolver(false)
        val temaerMedSpesifikkeLenkerIDev = innsynslenkerDev.keys
        val sakstemakoderMedGenerellLenkeIDev = Sakstemakode.values().toMutableSet()
            .minus(temaerMedSpesifikkeLenkerIDev)

        temaerMedSpesifikkeLenkerIDev.forEach { tema ->
            resolver.urlFor(tema) shouldBe innsynslenkerDev[tema]
        }

        sakstemakoderMedGenerellLenkeIDev.forEach { tema ->
            resolver.urlFor(tema).toString() shouldBe "$generellInnsynslenkeDev$tema"
        }
    }

}
