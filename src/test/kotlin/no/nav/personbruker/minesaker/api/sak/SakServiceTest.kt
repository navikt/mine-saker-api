package no.nav.personbruker.minesaker.api.sak

import io.mockk.*
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.minesaker.api.common.AuthenticatedUserObjectMother
import no.nav.personbruker.minesaker.api.common.IdportenUserObjectMother
import no.nav.personbruker.minesaker.api.common.exception.CommunicationException
import no.nav.personbruker.minesaker.api.digisos.DigiSosConsumer
import no.nav.personbruker.minesaker.api.digisos.DigiSosTokendings
import no.nav.personbruker.minesaker.api.domain.Fodselsnummer
import no.nav.personbruker.minesaker.api.domain.Sakstemakode
import no.nav.personbruker.minesaker.api.saf.SafConsumer
import no.nav.personbruker.minesaker.api.saf.SafTokendings
import no.nav.personbruker.minesaker.api.saf.journalposter.JournalposterRequest
import no.nav.personbruker.minesaker.api.saf.sakstemaer.SakstemaerRequest
import no.nav.personbruker.minesaker.api.tokenx.AccessToken
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be instance of`
import org.amshove.kluent.`should contain`
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SakServiceTest {

    private val dummyIdportenUser = IdportenUserObjectMother.createIdportenUser()
    private val dummyTokenXUser = AuthenticatedUserObjectMother.createTokenXUser()

    private val safDummyToken = AccessToken("saf<access_token>")
    private val digiSosDummyToken = AccessToken("digiSos<access_token>")

    private val safTokendings = mockk<SafTokendings>()
    private val digiSosTokendings = mockk<DigiSosTokendings>()

    @BeforeEach
    fun setup() {
        coEvery { safTokendings.exchangeToken(dummyIdportenUser) } returns safDummyToken
        coEvery { safTokendings.exchangeToken(dummyTokenXUser) } returns safDummyToken
        coEvery { digiSosTokendings.exchangeToken(dummyTokenXUser) } returns digiSosDummyToken
    }

    @AfterEach
    fun cleanup() {
        clearMocks(safTokendings, digiSosTokendings)
    }

    @Test
    fun `Skal hente alle sakstemaer for en konkret bruker`() {
        val safConsumer = mockk<SafConsumer>()
        val digiSosConsumer = mockk<DigiSosConsumer>()
        val service = SakService(safConsumer, safTokendings, digiSosConsumer, digiSosTokendings)

        val parameterSendtVidere = slot<SakstemaerRequest>()

        coEvery { safConsumer.hentSakstemaer(any(), any()) } returns SakstemaResultObjectMother.createSafResults()
        coEvery { digiSosConsumer.hentSakstemaer(any()) } returns SakstemaResultObjectMother.createDigiSosResults()

        runBlocking {
            service.hentSakstemaer(dummyTokenXUser)
        }

        coVerify(exactly = 1) { safConsumer.hentSakstemaer(capture(parameterSendtVidere), any()) }
        coVerify(exactly = 1) { digiSosConsumer.hentSakstemaer(any()) }

        parameterSendtVidere.captured `should be instance of` SakstemaerRequest::class

        confirmVerified(safConsumer)
        confirmVerified(digiSosConsumer)
    }

    @Test
    fun `Hvis en kilde feiler, returner data fra kilden som svarte og send med info om kilden som feilet`() {
        val safConsumer = mockk<SafConsumer>()
        val digiSosConsumer = mockk<DigiSosConsumer>()
        val service = SakService(safConsumer, safTokendings, digiSosConsumer, digiSosTokendings)

        coEvery { safConsumer.hentSakstemaer(any(), any()) } returns SakstemaResultObjectMother.createSafResults()
        coEvery { digiSosConsumer.hentSakstemaer(any()) } returns SakstemaResultObjectMother.createDigiSosError()

        val result = runBlocking {
            service.hentSakstemaer(dummyTokenXUser)
        }

        result.hasErrors() `should be equal to` true
        result.errors().`should contain`(Kildetype.DIGISOS)
        result.resultsSorted().shouldNotBeEmpty()
    }

    @Test
    fun `Skal hente alle journalposter for et konkret sakstema`() {
        val expectedSakstemakode = Sakstemakode.FOR

        val safConsumer = mockk<SafConsumer>(relaxed = true)
        val digiSosConsumer = mockk<DigiSosConsumer>()
        val service = SakService(safConsumer, safTokendings, digiSosConsumer, digiSosTokendings)

        val parameterSendtVidere = slot<JournalposterRequest>()

        runBlocking {
            service.hentJournalposterForSakstema(dummyIdportenUser, expectedSakstemakode)
        }

        coVerify(exactly = 1) {
            safConsumer.hentJournalposter(
                Fodselsnummer(dummyIdportenUser.ident),
                capture(parameterSendtVidere),
                safDummyToken
            )
        }

        parameterSendtVidere.captured `should be instance of` JournalposterRequest::class
        parameterSendtVidere.captured.variables.entries.toString() `should contain` expectedSakstemakode.toString()

        confirmVerified(safConsumer)
    }

    @Test
    fun `Feil som oppstaar, ved henting av journalposter for et konkret sakstema, skal kastes videre`() {
        val expectedException = CommunicationException("Simulert feil i en test")

        val safConsumer = mockk<SafConsumer>(relaxed = true)
        val digiSosConsumer = mockk<DigiSosConsumer>()
        val service = SakService(safConsumer, safTokendings, digiSosConsumer, digiSosTokendings)

        coEvery {
            safConsumer.hentJournalposter(Fodselsnummer(dummyIdportenUser.ident), any(), any())
        } throws expectedException

        val result = runCatching {
            runBlocking {
                val dummykode = Sakstemakode.FOR
                service.hentJournalposterForSakstema(dummyIdportenUser, dummykode)
            }
        }

        result.isFailure `should be equal to` true
        result.exceptionOrNull() `should be instance of` CommunicationException::class
    }

}
