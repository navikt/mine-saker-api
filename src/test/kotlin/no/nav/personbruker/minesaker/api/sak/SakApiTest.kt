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
import no.nav.personbruker.minesaker.api.digisos.DigiSosTokendings
import no.nav.personbruker.minesaker.api.domain.AuthenticatedUser
import no.nav.personbruker.minesaker.api.domain.ForenkletSakstema
import no.nav.personbruker.minesaker.api.domain.Sakstemakode
import no.nav.personbruker.minesaker.api.saf.SafConsumer
import no.nav.personbruker.minesaker.api.saf.SafTokendings
import no.nav.personbruker.minesaker.api.sak.ForventetSakstemaInnhold.Companion.toDigisosResponse
import no.nav.tms.token.support.authentication.installer.mock.installMockedAuthenticators
import no.nav.tms.token.support.idporten.sidecar.mock.SecurityLevel
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser
import org.junit.jupiter.api.Test
import java.net.URL
import java.time.LocalDateTime
import java.time.ZonedDateTime


class SakApiTest {

    private val objectMapper = ObjectMapper()
    private val testBaseUrl = "https://digisos.test.host"

    private val safTokendings = mockk<SafTokendings>().also {
        coEvery { it.exchangeToken(any<IdportenUser>()) } returns "<idportentoken>"
        coEvery { it.exchangeToken(any<AuthenticatedUser>()) } returns "<loginservicetoken>"
    }
    private val digsosTokendings = mockk<DigiSosTokendings>().also {
        coEvery { it.exchangeToken(any<IdportenUser>()) } returns "<idportentoken>"
        coEvery { it.exchangeToken(any<AuthenticatedUser>()) } returns "<loginservicetoken>"

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

        mockApi(
            sakService = SakService(
                safConsumer = safConsumer,
                safTokendings = safTokendings,
                digiSosConsumer = digisosConsumer,
                digiSosTokendings = digsosTokendings
            ),
            httpClient = appClient,
            sakerUrl = "http://mine.saker.dev"
        )

        setupExternalServices(
            hostApiBase = testBaseUrl,
            digissosEndpoint = "/minesaker/innsendte",
            content = listOf(aapSak, dagSak, hjeSak).toDigisosResponse()
        )

        client.get("/mine-saker-api/siste").apply {
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
    httpClient: HttpClient,
    corsAllowedOrigins: String = "*",
    corsAllowedSchemes: String = "*",
    rootPath: String = "mine-saker-api",
    authConfig: Application.() -> Unit = {
        installMockedAuthenticators {
            installIdPortenAuthMock {
                alwaysAuthenticated = true
                setAsDefault = true
                staticSecurityLevel = SecurityLevel.LEVEL_4
                staticUserPid = "testfnr"

            }
        }
    },
    sakerUrl: String = "http://minesaker.dev"
) = application {
    mineSakerApi(
        sakService = sakService,
        httpClient = httpClient,
        corsAllowedOrigins = corsAllowedOrigins,
        corsAllowedSchemes = corsAllowedSchemes,
        rootPath = rootPath,
        authConfig = authConfig,
        sakerUrl = sakerUrl
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
