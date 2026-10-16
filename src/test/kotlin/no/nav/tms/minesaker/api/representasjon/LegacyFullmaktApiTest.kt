package no.nav.tms.minesaker.api.representasjon

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.install
import io.ktor.server.auth.*
import io.ktor.server.request.receive
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.tms.minesaker.api.setup.jsonConfig
import no.nav.tms.minesaker.api.mineSakerApi
import no.nav.tms.minesaker.api.representasjon.Representasjonstype.Fullmakt
import no.nav.tms.minesaker.api.representasjon.Representasjonstype.Verge
import no.nav.tms.minesaker.api.setup.SystemTokenFetcher
import no.nav.tms.minesaker.api.setup.TokendingsExchange
import no.nav.tms.token.support.user.token.verification.Issuer
import no.nav.tms.token.support.user.token.verification.LevelOfAssurance
import no.nav.tms.token.support.user.token.verificaton.mock.userTokenMock
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.text.DateFormat


// Tester er identiske som i ReprApiTest, men bruker gammelt /fullmakt-endepunkt
class LegacyFullmaktApiTest {

    private val reprApiUrl = "http://repr"
    private val reprApiToken = "<repr_token>"

    private val pdlApiUrl = "http://pdl"
    private val pdlApiToken = "<pdl_token>"

    private val tokenExchanger = mockk<TokendingsExchange>().also {
        coEvery { it.reprApiToken(any()) } returns reprApiToken
        coEvery { it.pdlApiToken(any()) } returns pdlApiToken
    }

    private val tokenFetcher = mockk<SystemTokenFetcher>().also {
        coEvery { it.pdlApiToken() } returns pdlApiToken
    }

    private val sessionStore = ReprTestSessionStore()

    private val ident = "123"
    private val navn = "Innlogget Bruker"


    private val representert1 = Person("111", "Ene Einartsen")
    private val representert2 = Person("222", "Thor Tordivel")
    private val representert3 = Person("333", "Trevor Drei Trinidad")

    @AfterEach
    fun cleanUp() = runBlocking{
        sessionStore.clearRepresentert(ident)
    }

    @Test
    fun `henter info om fullmakt for sesjon`() = fullmaktApiTest {

        client.get("/fullmakt/info").validateResponse { json ->
            json["viserRepresentertesData"].asBoolean() shouldBe false
            json["representertIdent"].isNull shouldBe true
            json["representertNavn"].isNull shouldBe true
        }

        setRepresentert(representert1, Fullmakt)

        client.get("/fullmakt/info").validateResponse { json ->
            json["viserRepresentertesData"].asBoolean() shouldBe true
            json["representertIdent"].asText() shouldBe representert1.ident
            json["representertNavn"].asText() shouldBe representert1.navn
            json["forsholdstype"].asText() shouldBe Fullmakt.name
        }

        setRepresentert(representert2, Verge)

        client.get("/fullmakt/info").validateResponse { json ->
            json["viserRepresentertesData"].asBoolean() shouldBe true
            json["representertIdent"].asText() shouldBe representert2.ident
            json["representertNavn"].asText() shouldBe representert2.navn
            json["forsholdstype"].asText() shouldBe Verge.name
        }
    }

    @Test
    fun `henter gjeldende forhold for bruker`() = fullmaktApiTest {

        client.get("/fullmakt/forhold").validateResponse { json ->
            json["ident"].asText() shouldBe ident
            json["navn"].asText() shouldBe navn
            json["fullmaktsGivere"].size() shouldBe 0
        }

        updateReprResponse(
            fullmaktsgivere = listOf(
                representert1.ident,
                representert2.ident,
            ),
            verger = listOf(
                representert3.ident
            )
        )

        client.get("/fullmakt/forhold").validateResponse { json ->
            json["ident"].asText() shouldBe ident
            json["navn"].asText() shouldBe navn
            json["fullmaktsGivere"].size() shouldBe 2
            json["verger"].size() shouldBe 1
        }
    }

    @Test
    fun `setter fullmaktsgiver for sesjon hvis fullmaktsforhold er gyldig`() = fullmaktApiTest {
        updateReprResponse(
            verger = emptyList(),
            fullmaktsgivere = listOf(representert1.ident)
        )

        client.post("/fullmakt/representert") {
            setBody("""{"ident": "${representert1.ident}", "type": "Fullmakt"}""")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }

        sessionStore.getCurrentRepresentert(ident).let { giver ->
            giver.shouldNotBeNull()
            giver.ident shouldBe representert1.ident
            giver.navn shouldBe representert1.navn
            giver.type shouldBe Fullmakt
        }
    }

    @Test
    fun `setter verge for sesjon hvis forhold er gyldig`() = fullmaktApiTest {
        updateReprResponse(
            verger = listOf(representert2.ident),
            fullmaktsgivere = emptyList()
        )

        client.post("/fullmakt/representert") {
            setBody("""{"ident": "${representert2.ident}", "type": "Verge"}""")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }

        sessionStore.getCurrentRepresentert(ident).let { giver ->
            giver.shouldNotBeNull()
            giver.ident shouldBe representert2.ident
            giver.navn shouldBe representert2.navn
            giver.type shouldBe Verge
        }
    }

    @Test
    fun `bruker fullmakt som default representasjonstype dersom intet annet er spesifisert`() = fullmaktApiTest {
        updateReprResponse(
            verger = emptyList(),
            fullmaktsgivere = listOf(representert1.ident)
        )

        client.post("/fullmakt/representert") {
            setBody("""{"ident": "${representert1.ident}"}""")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }

        sessionStore.getCurrentRepresentert(ident).let { giver ->
            giver.shouldNotBeNull()
            giver.type shouldBe Fullmakt
        }
    }

    @Test
    fun `svarer med feil dersom en setter aktivt forhold som ikke er gyldig`() = fullmaktApiTest {
        updateReprResponse(
            verger = emptyList(),
            fullmaktsgivere = listOf(representert3.ident)
        )

        val response = client.post("/fullmakt/representert") {
            setBody("""{"ident": "${representert1.ident}"}""")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }

        response.status shouldBe HttpStatusCode.Forbidden

        sessionStore.getCurrentRepresentert(ident).shouldBeNull()
    }

    private suspend fun setRepresentert(person: Person, type: Representasjonstype) {
        sessionStore.setRepresentert(ident, Representert(person.navn, person.ident, type))
    }

    private fun fullmaktApiTest(testBlock: suspend ApplicationTestBuilder.(HttpClient) -> Unit) = testApplication {
        val testClient = createClient {
            install(ContentNegotiation) {
                jackson {
                    jsonConfig()
                }
            }
            install(HttpTimeout)
        }

        val navnFetcher = NavnFetcher(
            client = testClient,
            pdlUrl = pdlApiUrl,
            pdlBehandlingsnummer = "123",
            tokendingsExchange = tokenExchanger,
            systemTokenFetcher = tokenFetcher
        )

        val reprConsumer = ReprConsumer(
            httpClient = testClient,
            tokendingsExchange = tokenExchanger,
            reprApi = reprApiUrl
        )

        setupPdlApi(
            Person(ident, navn),
            representert1,
            representert2,
            representert3,
        )

        setupReprApi()

        application {
            mineSakerApi(
                safService = mockk(),
                digiSosConsumer = mockk(),
                httpClient = testClient,
                corsAllowedOrigins = "*",
                reprService = ReprService(reprConsumer, navnFetcher),
                reprSessionStore = sessionStore,
                authConfig = {
                    authentication {
                        userTokenMock {
                            levelOfAssurance = LevelOfAssurance.High
                            enableDefaultAuthentication {
                                tokenIssuer = Issuer.IdPorten
                                tokenIdent = ident
                            }
                        }
                    }
                }
            )
        }

        testBlock(testClient)
    }

    private fun ApplicationTestBuilder.setupPdlApi(vararg personer: Person) {
        externalServices {
            hosts(pdlApiUrl) {
                install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
                    jackson {
                        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        registerModule(JavaTimeModule())
                        dateFormat = DateFormat.getDateTimeInstance()
                    }
                }
                routing {
                    post("/graphql") {
                        val query = call.receive<PdlRequest>()

                        val response = if (query.variant == PdlRequest.Variant.HentPerson) {
                            val responsePerson = personer.first { it.ident == query.ident }

                            hentPersonTestResponse(responsePerson)
                        } else {
                            val responsePersoner = personer.filter { query.identer.contains(it.ident) }

                            hentPersonBolkTestResponse(responsePersoner)
                        }

                        call.respondText(response, contentType = ContentType.Application.Json)
                    }
                }
            }
        }
    }

    private lateinit var _reprResponse: String

    private fun updateReprResponse(verger: List<String>, fullmaktsgivere: List<String>) {
        _reprResponse = reprTestResponse(verger, fullmaktsgivere)
    }

    private fun ApplicationTestBuilder.setupReprApi() {
        _reprResponse = reprTestResponse(emptyList(), emptyList())

        externalServices {
            hosts(reprApiUrl) {
                install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
                    jackson {
                        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        registerModule(JavaTimeModule())
                        dateFormat = DateFormat.getDateTimeInstance()
                    }
                }
                routing {
                    get("/api/v2/eksternbruker/kan-representere") {
                        call.respondText(_reprResponse, contentType = ContentType.Application.Json)
                    }
                }
            }
        }
    }

    private data class PdlRequest(
        private val variables: Variables
    ) {
        val ident get() = variables.ident!!
        val identer get() = variables.identer!!

        val variant get() = if (variables.ident != null) {
            Variant.HentPerson
        } else if (variables.identer != null) {
            Variant.HentPersonBolk
        } else {
            throw IllegalStateException()
        }

        data class Variables(
            val ident: String? = null,
            val identer: List<String>? = null
        )

        enum class Variant {
            HentPerson, HentPersonBolk
        }
    }

    private val objectMapper = jacksonObjectMapper()

    private suspend fun HttpResponse.validateResponse(validator: (JsonNode) -> Unit) =
        bodyAsText()
            .let { objectMapper.readTree(it) }
            .let { validator(it) }

}
