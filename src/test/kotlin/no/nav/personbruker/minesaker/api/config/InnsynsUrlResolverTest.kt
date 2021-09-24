package no.nav.personbruker.minesaker.api.config

import no.nav.personbruker.minesaker.api.domain.Sakstemakode
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

internal class InnsynsUrlResolverTest {

    @Test
    fun `Skal returnere prod-lenker for alle sakstemaer`() {
        val resolver = InnsynsUrlResolver(true)
        val temaerMedSpesifikkeLenkerIProd = innsynslenkerProd.keys
        val sakstemakoderMedGenerellLenkeIProd = Sakstemakode.values().toMutableSet()
            .minus(temaerMedSpesifikkeLenkerIProd)

        temaerMedSpesifikkeLenkerIProd.forEach { tema ->
            resolver.urlFor(tema) `should be equal to` innsynslenkerProd[tema]
        }

        sakstemakoderMedGenerellLenkeIProd.forEach { tema ->
            resolver.urlFor(tema) `should be equal to` generellInnsynslenkeProd
        }
    }

    @Test
    fun `Skal returnere dev-lenker for alle sakstemaer`() {
        val resolver = InnsynsUrlResolver(false)
        val temaerMedSpesifikkeLenkerIDev = innsynslenkerDev.keys
        val sakstemakoderMedGenerellLenkeIDev = Sakstemakode.values().toMutableSet()
            .minus(temaerMedSpesifikkeLenkerIDev)

        temaerMedSpesifikkeLenkerIDev.forEach { tema ->
            resolver.urlFor(tema) `should be equal to` innsynslenkerDev[tema]
        }

        sakstemakoderMedGenerellLenkeIDev.forEach { tema ->
            resolver.urlFor(tema) `should be equal to` generellInnsynslenkeDev
        }
    }

}
