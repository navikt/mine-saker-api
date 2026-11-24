package no.nav.tms.minesaker.api.journalpost

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.matchers.shouldBe
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.auth.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.mockk
import no.nav.dokument.saf.selvbetjening.generated.dto.AlleJournalposter
import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalpost
import no.nav.dokument.saf.selvbetjening.generated.dto.allejournalposter.Dokumentoversikt
import no.nav.dokument.saf.selvbetjening.generated.dto.enums.Journalposttype
import no.nav.dokument.saf.selvbetjening.generated.dto.enums.Variantformat
import no.nav.dokument.saf.selvbetjening.generated.dto.hentjournalpost.DokumentInfo
import no.nav.dokument.saf.selvbetjening.generated.dto.hentjournalpost.Dokumentvariant
import no.nav.tms.minesaker.api.journalpost.query.GraphQLResponse
import no.nav.tms.minesaker.api.mineSakerApi
import no.nav.tms.minesaker.api.representasjon.ReprTestSessionStore
import no.nav.tms.minesaker.api.setup.TokendingsExchange
import no.nav.tms.minesaker.api.setup.createUrl
import no.nav.tms.minesaker.api.setup.jsonConfig
import no.nav.tms.token.support.user.token.verification.Issuer
import no.nav.tms.token.support.user.token.verification.LevelOfAssurance
import no.nav.tms.token.support.user.token.verificaton.mock.userTokenMock
import org.junit.jupiter.api.Test
import no.nav.dokument.saf.selvbetjening.generated.dto.hentjournalpost.Journalpost as HentSafJournalpost

class SorteringsdatoApiTest {

    private val objectMapper = jacksonObjectMapper()

    private val datoFraSaf = "2025-01-15T00:30:00"
    private val forventetDatoFraApi = "2025-01-14T23:30:00Z"

    @Test
    fun `alle journalposter returnerer sorteringsdato fra SAF konvertert fra norsk tid til UTC`() =
        journalpostApiTest(safSvar = alleJournalposterFraSaf(datoFraSaf)) { client ->
            val journalpost = client.get("v2/journalposter/alle").json().first()

            journalpost["sorteringsdato"].asText() shouldBe forventetDatoFraApi
        }

    @Test
    fun `hent journalpost returnerer sorteringsdato fra SAF konvertert fra norsk tid til UTC`() =
        journalpostApiTest(safSvar = hentJournalpostFraSaf(datoFraSaf)) { client ->
            val journalpost = client.get("v2/journalposter/journalpost/123").json()

            journalpost["sorteringsdato"].asText() shouldBe forventetDatoFraApi
        }

    @Test
    fun `siste journalposter returnerer sorteringsdato fra SAF konvertert fra norsk tid til UTC`() =
        journalpostApiTest(safSvar = alleJournalposterFraSaf(datoFraSaf)) { client ->
            val journalpost = client.get("v2/journalposter/siste").json().first()

            journalpost["sorteringsdato"].asText() shouldBe forventetDatoFraApi
        }

    private suspend fun HttpResponse.json(): JsonNode {
        status shouldBe HttpStatusCode.OK
        return objectMapper.readTree(bodyAsText())
    }

    private fun alleJournalposterFraSaf(datoSortering: String) = AlleJournalposter.Result(
        Dokumentoversikt(listOf(JournalpostTestData.inngaaendeDokument(sorteringsdato = datoSortering)))
    )

    private fun hentJournalpostFraSaf(datoSortering: String) = HentJournalpost.Result(
        HentSafJournalpost(
            tittel = "Dummytittel",
            tema = "AAP",
            journalpostId = "123",
            journalposttype = Journalposttype.I,
            datoSortering = datoSortering,
            dokumenter = listOf(
                DokumentInfo(
                    tittel = "Dummytittel med arkivert",
                    dokumentInfoId = "dummyId001",
                    dokumentvarianter = listOf(
                        Dokumentvariant(Variantformat.ARKIV, true, listOf("ok"), "PDF", 1_000_000)
                    )
                )
            )
        )
    )

    private fun journalpostApiTest(safSvar: Any, testBlock: suspend (HttpClient) -> Unit) = testApplication {
        val safHttpClient = HttpClient(MockEngine) {
            engine {
                addHandler {
                    respond(
                        objectMapper.writeValueAsString(GraphQLResponse(safSvar)),
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    )
                }
            }
            install(ContentNegotiation) {
                jackson {
                    jsonConfig()
                }
            }
            install(HttpTimeout)
        }

        val safService = SafService(
            safConsumer = SafConsumer(safHttpClient, safEndpoint = createUrl("https://www.dummy.no")),
            tokendingsExchange = mockk<TokendingsExchange>().also {
                coEvery { it.safToken(any()) } returns "<dummytoken>"
            }
        )

        application {
            mineSakerApi(
                safService = safService,
                digiSosConsumer = mockk(),
                httpClient = safHttpClient,
                corsAllowedOrigins = "*",
                reprService = mockk(),
                reprSessionStore = ReprTestSessionStore(),
                authConfig = {
                    authentication {
                        userTokenMock {
                            levelOfAssurance = LevelOfAssurance.High
                            enableDefaultAuthentication {
                                tokenIssuer = Issuer.IdPorten
                                tokenIdent = "123"
                            }
                        }
                    }
                }
            )
        }

        testBlock(client)
    }
}
