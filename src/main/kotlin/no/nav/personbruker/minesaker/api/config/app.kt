package no.nav.personbruker.minesaker.api.config

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.personbruker.minesaker.api.digisos.DigiSosConsumer
import no.nav.personbruker.minesaker.api.saf.SafConsumer
import no.nav.personbruker.minesaker.api.saf.fullmakt.FullmaktConsumer
import no.nav.personbruker.minesaker.api.saf.fullmakt.FullmaktInterception
import no.nav.personbruker.minesaker.api.saf.fullmakt.FullmaktService
import no.nav.personbruker.minesaker.api.saf.fullmakt.FullmaktJwtService
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
        digiSosClientId = environment.digiSosClientId,
        pdlFullmaktClientId = environment.pdlFullmaktClientId
    )

    val fullmaktConsumer = FullmaktConsumer(httpClient, tokendingsExchange, environment.pdlFullmaktUrl)
    val fullmaktService = FullmaktService(fullmaktConsumer)
    val fullmektigJwtService = FullmaktJwtService(environment.fullmaktJwtIssuer, environment.fullmaktPrivateJwk)
    val fullmaktInterception = FullmaktInterception(fullmektigJwtService)

    val safConsumer = SafConsumer(httpClient, environment.safEndpoint, innsynsUrlResolver)
    val digiSosConsumer = DigiSosConsumer(httpClient, environment.digiSosEndpoint, innsynsUrlResolver)
    val sakService = SakService(safConsumer, tokendingsExchange, digiSosConsumer)

    embeddedServer(
        factory = Netty,
        environment = applicationEngineEnvironment {
            rootPath = "mine-saker-api"

            module {
                mineSakerApi(
                    sakService = sakService,
                    sakerUrl = environment.sakerUrl,
                    httpClient = httpClient,
                    corsAllowedOrigins = environment.corsAllowedOrigins,
                    corsAllowedSchemes = environment.corsAllowedSchemes,
                    authConfig = authConfig(),
                    fullmaktService = fullmaktService,
                    fullmaktInterception = fullmaktInterception,
                    fullmaktJwtService = fullmektigJwtService
                )
            }
            connector {
                port = 8080
            }
        }
    ).start(wait = true)
}

