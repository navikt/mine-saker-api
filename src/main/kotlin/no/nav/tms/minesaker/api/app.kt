package no.nav.tms.minesaker.api

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.tms.minesaker.api.innsendte.DigiSosConsumer
import no.nav.tms.minesaker.api.journalpost.SafConsumer
import no.nav.tms.minesaker.api.fullmakt.FullmaktConsumer
import no.nav.tms.minesaker.api.fullmakt.FullmaktRedis
import no.nav.tms.minesaker.api.fullmakt.FullmaktService
import no.nav.tms.minesaker.api.fullmakt.NavnFetcher
import no.nav.tms.minesaker.api.journalpost.SafService
import no.nav.tms.minesaker.api.setup.Environment
import no.nav.tms.minesaker.api.setup.HttpClientBuilder
import no.nav.tms.minesaker.api.setup.TokendingsExchange
import no.nav.tms.token.support.tokendings.exchange.TokendingsServiceBuilder

fun main() {
    val environment = Environment()
    val httpClient = HttpClientBuilder.build()

    val tokendingsService = TokendingsServiceBuilder.buildTokendingsService(maxCachedEntries = 10000)

    val tokendingsExchange = TokendingsExchange(
        tokendingsService = tokendingsService,
        safselvbetjeningClientId = environment.safClientId,
        digiSosClientId = environment.digiSosClientId,
        pdlFullmaktClientId = environment.reprFullmaktClientId,
        pdlApiClientId = environment.pdlApiClientId
    )

    val navnFetcher = NavnFetcher(httpClient, environment.pdlApiUrl, environment.pdlBehandlingsnummer, tokendingsExchange)

    val fullmaktConsumer = FullmaktConsumer(httpClient, tokendingsExchange, environment.reprFullmaktUrl)
    val fullmaktService = FullmaktService(fullmaktConsumer, navnFetcher)
    val fullmaktSessionStore = FullmaktRedis()

    val safConsumer = SafConsumer(httpClient, environment.safEndpoint)
    val digiSosConsumer = DigiSosConsumer(httpClient, tokendingsExchange, environment.digiSosEndpoint)
    val sakService = SafService(safConsumer, tokendingsExchange, digiSosConsumer)

    embeddedServer(
        factory = Netty,
        module =  {
            rootPath = "mine-saker-api"

            mineSakerApi(
                sakService = sakService,
                digiSosConsumer = digiSosConsumer,
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

