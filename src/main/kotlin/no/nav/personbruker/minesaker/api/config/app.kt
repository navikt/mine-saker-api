package no.nav.personbruker.minesaker.api.config

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.personbruker.minesaker.api.digisos.DigiSosConsumer
import no.nav.personbruker.minesaker.api.digisos.DigiSosTokendings
import no.nav.personbruker.minesaker.api.saf.SafConsumer
import no.nav.personbruker.minesaker.api.saf.SafTokendings
import no.nav.personbruker.minesaker.api.sak.SakService
import no.nav.tms.token.support.tokendings.exchange.TokendingsServiceBuilder

fun main() {
    val environment = Environment()
    val httpClient = HttpClientBuilder.build()

    val tokendingsService = TokendingsServiceBuilder.buildTokendingsService(maxCachedEntries = 10000)

    val safTokendings = SafTokendings(tokendingsService, environment.safClientId)
    val digiSosTokendings = DigiSosTokendings(tokendingsService, environment.digiSosClientId)

    val safConsumer = SafConsumer(httpClient, environment.safEndpoint)
    val digiSosConsumer = DigiSosConsumer(httpClient, environment.digiSosEndpoint)
    val sakService = SakService(safConsumer, safTokendings, digiSosConsumer, digiSosTokendings)

    embeddedServer(Netty, port = environment.port) {
        mineSakerApi(
            sakService,
            httpClient,
            environment.corsAllowedOrigins,
            environment.corsAllowedSchemes,
            environment.rootPath,
            authConfig(environment.rootPath),
        )
    }.start(wait = true)
}

