package no.nav.tms.minesaker.api

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.tms.minesaker.api.digisos.DigiSosConsumer
import no.nav.tms.minesaker.api.saf.SafConsumer
import no.nav.tms.minesaker.api.saf.fullmakt.FullmaktConsumer
import no.nav.tms.minesaker.api.saf.fullmakt.FullmaktRedis
import no.nav.tms.minesaker.api.saf.fullmakt.FullmaktService
import no.nav.tms.minesaker.api.saf.fullmakt.NavnFetcher
import no.nav.tms.minesaker.api.setup.Environment
import no.nav.tms.minesaker.api.setup.HttpClientBuilder
import no.nav.tms.minesaker.api.saf.InnsynsUrlResolver
import no.nav.tms.minesaker.api.setup.TokendingsExchange
import no.nav.tms.token.support.tokendings.exchange.TokendingsServiceBuilder

fun main() {
    val environment = Environment()
    val httpClient = HttpClientBuilder.build()

    val innsynsUrlResolver = InnsynsUrlResolver(environment.innsynsLenker, environment.defaultInnsynLenke)

    val tokendingsService = TokendingsServiceBuilder.buildTokendingsService(maxCachedEntries = 10000)

    val tokendingsExchange = TokendingsExchange(
        tokendingsService = tokendingsService,
        safselvbetjeningClientId = environment.safClientId,
        digiSosClientId = environment.digiSosClientId,
        pdlFullmaktClientId = environment.pdlFullmaktClientId,
        pdlApiClientId = environment.pdlApiClientId
    )

    val navnFetcher = NavnFetcher(httpClient, environment.pdlApiUrl, environment.pdlBehandlingsnummer, tokendingsExchange)
    val fullmaktConsumer = FullmaktConsumer(httpClient, tokendingsExchange, environment.pdlFullmaktUrl)
    val fullmaktService = FullmaktService(fullmaktConsumer, navnFetcher)
    val fullmaktSessionStore = FullmaktRedis()

    val safConsumer = SafConsumer(httpClient, environment.safEndpoint, innsynsUrlResolver)
    val digiSosConsumer = DigiSosConsumer(httpClient, environment.digiSosEndpoint, innsynsUrlResolver)
    val sakService = SakService(safConsumer, tokendingsExchange, digiSosConsumer)

    embeddedServer(
        factory = Netty,
        module =  {
            rootPath = "mine-saker-api"

            mineSakerApi(
                sakService = sakService,
                sakerUrl = environment.sakerUrl,
                httpClient = httpClient,
                corsAllowedOrigins = environment.corsAllowedOrigins,
                authConfig = authConfig(),
                fullmaktService = fullmaktService,
                fullmaktSessionStore = fullmaktSessionStore
            )
        },
        configure = {
            connector {
                port = 8080
            }
        }
    ).start(wait = true)
}

