package no.nav.tms.minesaker.api.sak;

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.coEvery
import io.mockk.mockk
import no.nav.tms.common.testutils.RouteProvider
import no.nav.tms.common.testutils.initExternalServices
import no.nav.tms.minesaker.api.SakService
import no.nav.tms.minesaker.api.SubstantialAuth
import no.nav.tms.minesaker.api.setup.jsonConfig
import no.nav.tms.minesaker.api.mineSakerApi
import no.nav.tms.minesaker.api.digisos.DigiSosConsumer
import no.nav.tms.minesaker.api.saf.sakstemaer.Sakstemakode
import no.nav.tms.minesaker.api.saf.SafConsumer
import no.nav.tms.minesaker.api.setup.TokendingsExchange
import no.nav.tms.minesaker.api.saf.fullmakt.FullmaktService
import no.nav.tms.minesaker.api.saf.fullmakt.FullmaktSessionStore
import no.nav.tms.minesaker.api.saf.fullmakt.FullmaktTestSessionStore
import no.nav.tms.minesaker.api.saf.sakstemaer.ForenkletSakstema
import no.nav.tms.minesaker.api.saf.sakstemaer.SakstemaResult
import no.nav.tms.minesaker.api.sak.ForventetSakstemaInnhold.Companion.toDigisosResponse
import no.nav.tms.minesaker.api.setup.createUrl
import no.nav.tms.token.support.idporten.sidecar.mock.LevelOfAssurance
import no.nav.tms.token.support.idporten.sidecar.mock.idPortenMock
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
            digiSosEndpoint = createUrl(testBaseUrl), innsynsUrlResolver = testInnsynsUrlResolver
        )

        val fullmaktService = mockk<FullmaktService>()
        val fullmaktSessionStore = FullmaktTestSessionStore()

        mockApi(
            sakService = SakService(
                safConsumer = safConsumer,
                tokendingsExchange = tokendingsExchange,
                digiSosConsumer = digisosConsumer
            ),
            httpClient = appClient,
            sakerUrl = "http://mine.saker.dev",
            fullmaktService = fullmaktService,
            fullmaktSessionStore = fullmaktSessionStore
        )

        initExternalServices(testBaseUrl,
            object : RouteProvider(path = "/minesaker/innsendte", routeMethodFunction = Routing::get) {
                override fun content() = listOf(aapSak, dagSak, hjeSak).toDigisosResponse()
            })

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
    fullmaktSessionStore: FullmaktSessionStore,
    httpClient: HttpClient,
    corsAllowedOrigins: String = "*",
    corsAllowedSchemes: String = "*",
    authConfig: Application.() -> Unit = {
        authentication {
            idPortenMock {
                alwaysAuthenticated = true
                setAsDefault = true
                staticLevelOfAssurance = LevelOfAssurance.HIGH
                staticUserPid = "testfnr"
            }

            idPortenMock {
                authenticatorName = SubstantialAuth
                alwaysAuthenticated = true
                setAsDefault = false
                staticLevelOfAssurance = LevelOfAssurance.SUBSTANTIAL
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
        authConfig = authConfig,
        sakerUrl = sakerUrl,
        fullmaktService = fullmaktService,
        fullmaktSessionStore = fullmaktSessionStore
    )
}
