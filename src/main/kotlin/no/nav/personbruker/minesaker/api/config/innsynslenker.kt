package no.nav.personbruker.minesaker.api.config

import no.nav.personbruker.minesaker.api.domain.Sakstemakode
import java.net.URL

val generellInnsynslenkeDev = URL("https://person.dev.nav.no/mine-saker/tema/")
val innsynslenkerDev : Map<Sakstemakode, URL> = mapOf(
    Sakstemakode.DAG to URL("https://arbeid.dev.nav.no/arbeid/dagpenger/mine-dagpenger"),
    Sakstemakode.HJE to URL("https://hjelpemidler.dev.nav.no/hjelpemidler/dinehjelpemidler"),
    Sakstemakode.KOM to URL("https://www-q0.dev.nav.no/sosialhjelp/innsyn"),
    Sakstemakode.AAP to URL("https://aap-innsyn.dev.nav.no/aap/mine-aap/"),
)

val generellInnsynslenkeProd = URL("https://person.nav.no/mine-saker/tema/")
val innsynslenkerProd : Map<Sakstemakode, URL> = mapOf(
    Sakstemakode.DAG to URL("https://www.nav.no/arbeid/dagpenger/mine-dagpenger"),
    Sakstemakode.HJE to URL("https://www.nav.no/hjelpemidler/dinehjelpemidler"),
    Sakstemakode.KOM to URL("https://www.nav.no/sosialhjelp/innsyn"),
    Sakstemakode.AAP to URL("https://www.nav.no/aap/mine-aap"),
)
