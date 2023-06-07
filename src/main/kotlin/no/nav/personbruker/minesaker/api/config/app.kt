package no.nav.personbruker.minesaker.api.config

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.personbruker.minesaker.api.digisos.DigiSosConsumer
import no.nav.personbruker.minesaker.api.saf.SafConsumer
import no.nav.personbruker.minesaker.api.sak.SakService
import no.nav.tms.token.support.tokendings.exchange.TokendingsServiceBuilder

fun main() {
    val environment = Environment()
    val httpClient = HttpClientBuilder.build()
    val innsynsUrlResolver = InnsynsUrlResolver(environment.innsynsLenker, environment.defaultInnsynLenke)

    val tokendingsService = TokendingsServiceBuilder.buildTokendingsService(maxCachedEntries = 10000)

    val tokendingsExchange = TokendingsExchange(
        tokendingsService = tokendingsService,
        safselvbetjeningClientId = environment.safClientId,
        digiSosClientId = environment.digiSosClientId
    )

    val safConsumer = SafConsumer(httpClient, environment.safEndpoint, innsynsUrlResolver)
    val digiSosConsumer = DigiSosConsumer(httpClient, environment.digiSosEndpoint, innsynsUrlResolver)
    val sakService = SakService(safConsumer, tokendingsExchange, digiSosConsumer)

    embeddedServer(Netty, port = environment.port) {
        mineSakerApi(
            sakService = sakService,
            sakerUrl = environment.sakerUrl,
            httpClient = httpClient,
            corsAllowedOrigins = environment.corsAllowedOrigins,
            corsAllowedSchemes = environment.corsAllowedSchemes,
            rootPath = environment.rootPath,
            authConfig = authConfig(environment.rootPath),
        )
    }.start(wait = true)
}

