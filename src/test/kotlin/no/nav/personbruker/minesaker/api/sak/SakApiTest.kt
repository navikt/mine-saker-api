package no.nav.personbruker.minesaker.api.sak;

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.coEvery
import io.mockk.mockk
import no.nav.personbruker.minesaker.api.config.jsonConfig
import no.nav.personbruker.minesaker.api.config.mineSakerApi
import no.nav.personbruker.minesaker.api.digisos.DigiSosConsumer
import no.nav.personbruker.minesaker.api.domain.ForenkletSakstema
import no.nav.personbruker.minesaker.api.domain.Sakstemakode
import no.nav.personbruker.minesaker.api.saf.SafConsumer
import no.nav.personbruker.minesaker.api.config.TokendingsExchange
import no.nav.personbruker.minesaker.api.saf.fullmakt.FullmaktInterception
import no.nav.personbruker.minesaker.api.saf.fullmakt.FullmaktJwtService
import no.nav.personbruker.minesaker.api.saf.fullmakt.FullmaktService
import no.nav.personbruker.minesaker.api.sak.ForventetSakstemaInnhold.Companion.toDigisosResponse
import no.nav.tms.token.support.idporten.sidecar.mock.LevelOfAssurance
import no.nav.tms.token.support.idporten.sidecar.mock.installIdPortenAuthMock
import org.junit.jupiter.api.Test
import java.net.URL
import java.time.LocalDateTime
import java.time.ZonedDateTime


class SakApiTest {

    private val objectMapper = ObjectMapper()
    private val testBaseUrl = "https://digisos.test.host"

    private val tokendingsExchange = mockk<TokendingsExchange>().also {
        coEvery { it.safToken(any()) } returns "<saftoken>"
        coEvery { it.digisosToken(any()) } returns "<digisostoken>"
    }
    private val safConsumer = mockk<SafConsumer>().also {
        coEvery { it.hentSakstemaer(any(), any()) } returns SakstemaResult(
            results = listOf(
                komSak, sykSak
            ).toSafResponse(),
            errors = emptyList()
        )
    }

    private val expectedSakstema = listOf(aapSak, sykSak)

    @Test
    fun `henter siste`() = testApplication {

        val appClient = createClient {
            install(ContentNegotiation) {
                jackson {
                    jsonConfig()
                }
            }
            install(HttpTimeout)
        }
        val digisosConsumer = DigiSosConsumer(
            httpClient = appClient,
            digiSosEndpoint = URL(testBaseUrl), innsynsUrlResolver = testInnsynsUrlResolver
        )

        val fullmaktService = mockk<FullmaktService>()
        val fullmaktJwtService = mockk<FullmaktJwtService>()
        val fullmaktInterception = FullmaktInterception(fullmaktJwtService)

        mockApi(
            sakService = SakService(
                safConsumer = safConsumer,
                tokendingsExchange = tokendingsExchange,
                digiSosConsumer = digisosConsumer
            ),
            httpClient = appClient,
            sakerUrl = "http://mine.saker.dev",
            fullmaktService = fullmaktService,
            fullmaktJwtService = fullmaktJwtService,
            fullmaktInterception = fullmaktInterception
        )

        setupExternalServices(
            hostApiBase = testBaseUrl,
            digissosEndpoint = "/minesaker/innsendte",
            content = listOf(aapSak, dagSak, hjeSak).toDigisosResponse()
        )

        client.get("/siste").apply {
            status shouldBe HttpStatusCode.OK
            val response = objectMapper.readTree(bodyAsText())
            val sakstemaer = response["sakstemaer"].toList()
            expectedSakstema.forEach { assertSakstema(sakstemaer, it) }
            response["dagpengerSistEndret"].asLocalDateTime() shouldBe dagSak.sistEndret.toLocalDateTime()
            response["sakerURL"].asText() shouldBe "http://mine.saker.dev"
        }

    }

    private fun assertSakstema(node: List<JsonNode>, forventetSakstemaInnhold: ForventetSakstemaInnhold) {
        val resultat =
            node.find { it["kode"].asText() == forventetSakstemaInnhold.kode }
                .let { sakstema ->
                    require(sakstema != null)
                    sakstema
                }
        resultat["navn"].asText() shouldBe forventetSakstemaInnhold.navn
        resultat["kode"].asText() shouldBe forventetSakstemaInnhold.kode
        resultat["sistEndret"].asLocalDateTime() shouldBe forventetSakstemaInnhold.sistEndret.toLocalDateTime()
        resultat["detaljvisningUrl"].asText() shouldBe forventetSakstemaInnhold.detaljvisningUrl
    }


}

private fun List<ForventetSakstemaInnhold>.toSafResponse(): List<ForenkletSakstema> = map {
    ForenkletSakstema(
        navn = it.navn,
        kode = Sakstemakode.valueOf(it.kode),
        sistEndret = it.sistEndret,
        detaljvisningUrl = it.detaljvisningUrl
    )
}

private fun JsonNode?.asLocalDateTime(): LocalDateTime? = this?.let {
    ZonedDateTime.parse(it.asText()).toLocalDateTime()
}

private fun ApplicationTestBuilder.mockApi(
    sakService: SakService,
    fullmaktService: FullmaktService,
    fullmaktJwtService: FullmaktJwtService,
    fullmaktInterception: FullmaktInterception,
    httpClient: HttpClient,
    corsAllowedOrigins: String = "*",
    corsAllowedSchemes: String = "*",
    authConfig: Application.() -> Unit = {
            installIdPortenAuthMock {
                alwaysAuthenticated = true
                setAsDefault = true
                staticLevelOfAssurance = LevelOfAssurance.LEVEL_4
                staticUserPid = "testfnr"

            }
    },
    sakerUrl: String = "http://minesaker.dev"
) = application {


    mineSakerApi(
        sakService = sakService,
        httpClient = httpClient,
        corsAllowedOrigins = corsAllowedOrigins,
        corsAllowedSchemes = corsAllowedSchemes,
        authConfig = authConfig,
        sakerUrl = sakerUrl,
        fullmaktService = fullmaktService,
        fullmaktJwtService = fullmaktJwtService,
        fullmaktInterception = fullmaktInterception
    )
}


internal fun ApplicationTestBuilder.setupExternalServices(
    hostApiBase: String,
    digissosEndpoint: String,
    content: String
) {
    externalServices {
        hosts(hostApiBase) {
            routing {
                get(digissosEndpoint) {
                    call.respondBytes(
                        contentType = ContentType.Application.Json,
                        provider = { content.toByteArray() })
                }
            }
        }
    }
}

