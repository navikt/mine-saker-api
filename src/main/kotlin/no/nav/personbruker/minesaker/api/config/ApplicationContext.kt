package no.nav.personbruker.minesaker.api.config

import no.nav.personbruker.minesaker.api.digisos.DigiSosConsumer
import no.nav.personbruker.minesaker.api.digisos.DigiSosTokendings
import no.nav.personbruker.minesaker.api.saf.SafConsumer
import no.nav.personbruker.minesaker.api.saf.SafTokendings
import no.nav.personbruker.minesaker.api.sak.SakService
import no.nav.tms.token.support.tokendings.exchange.TokendingsServiceBuilder

class ApplicationContext {

    val environment = Environment()

    val httpClient = HttpClientBuilder.build()

    val tokendingsService = TokendingsServiceBuilder.buildTokendingsService(maxCachedEntries = 10000)

    val safTokendings = SafTokendings(tokendingsService, environment.safClientId)
    val digiSosTokendings = DigiSosTokendings(tokendingsService, environment.digiSosClientId)

    val safConsumer = SafConsumer(httpClient, environment.safEndpoint)
    val digiSosConsumer = DigiSosConsumer(httpClient, environment.digiSosEndpoint)
    val sakService = SakService(safConsumer, safTokendings, digiSosConsumer, digiSosTokendings)
}
