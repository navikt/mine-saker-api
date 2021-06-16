package no.nav.personbruker.minesaker.api.sak

import io.mockk.*
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.minesaker.api.common.IdportenUserObjectMother
import no.nav.personbruker.minesaker.api.common.exception.CommunicationException
import no.nav.personbruker.minesaker.api.saf.SafConsumer
import no.nav.personbruker.minesaker.api.domain.Fodselsnummer
import no.nav.personbruker.minesaker.api.domain.Sakstemakode
import no.nav.personbruker.minesaker.api.saf.SafTokendingsService
import no.nav.personbruker.minesaker.api.saf.journalposter.JournalposterRequest
import no.nav.personbruker.minesaker.api.saf.sakstemaer.SakstemaerRequest
import no.nav.personbruker.minesaker.api.tokenx.AccessToken
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be instance of`
import org.amshove.kluent.`should contain`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SakServiceTest {

    private val dummyUser = IdportenUserObjectMother.createIdportenUser()


    private val dummyToken = "<access_token>"
    private val dummyClientId = "<client_id>"
    private val tokendingsService: no.nav.tms.token.support.tokendings.exchange.TokendingsService = mockk()

    private val tokendingsWrapper =
        SafTokendingsService(tokendingsService, dummyClientId)

    @BeforeEach
    fun setup() {
        coEvery { tokendingsService.exchangeToken(any(), dummyClientId) } returns dummyToken
    }

    @AfterEach
    fun cleanup() {
        clearMocks(tokendingsService)
    }

    @Test
    fun `Skal hente alle sakstemaer for en konkret bruker`() {
        val consumer = mockk<SafConsumer>(relaxed = true)
        val service = SakService(consumer, tokendingsWrapper)

        val parameterSendtVidere = slot<SakstemaerRequest>()

        runBlocking {
            service.hentSakstemaer(dummyUser)
        }

        coVerify(exactly = 1) { consumer.hentSakstemaer(capture(parameterSendtVidere), AccessToken(dummyToken)) }

        parameterSendtVidere.captured `should be instance of` SakstemaerRequest::class

        confirmVerified(consumer)
    }

    @Test
    fun `Feil som oppstaar, ved henting av alle sakstemaer, skal kastes videre`() {
        val expectedException = CommunicationException("Simulert feil i en test")

        val consumer = mockk<SafConsumer>(relaxed = true)
        val service = SakService(consumer, tokendingsWrapper)

        coEvery {
            consumer.hentSakstemaer(any(), AccessToken(dummyToken))
        } throws expectedException

        val result = runCatching {
            runBlocking {
                service.hentSakstemaer(dummyUser)
            }
        }

        result.isFailure `should be equal to` true
        result.exceptionOrNull() `should be instance of` CommunicationException::class
    }

    @Test
    fun `Skal hente alle journalposter for et konkret sakstema`() {
        val expectedSakstemakode = Sakstemakode.FOR

        val consumer = mockk<SafConsumer>(relaxed = true)
        val service = SakService(consumer, tokendingsWrapper)

        val parameterSendtVidere = slot<JournalposterRequest>()

        runBlocking {
            service.hentJournalposterForSakstema(dummyUser, expectedSakstemakode)
        }

        coVerify(exactly = 1) { consumer.hentJournalposter(Fodselsnummer(dummyUser.ident), capture(parameterSendtVidere), AccessToken(dummyToken)) }

        parameterSendtVidere.captured `should be instance of` JournalposterRequest::class
        parameterSendtVidere.captured.variables.entries.toString() `should contain` expectedSakstemakode.toString()

        confirmVerified(consumer)
    }

    @Test
    fun `Feil som oppstaar, ved henting av journalposter for et konkret sakstema, skal kastes videre`() {
        val expectedException = CommunicationException("Simulert feil i en test")

        val consumer = mockk<SafConsumer>(relaxed = true)
        val service = SakService(consumer, tokendingsWrapper)

        coEvery {
            consumer.hentJournalposter(Fodselsnummer(dummyUser.ident), any(), AccessToken(any()))
        } throws expectedException

        val result = runCatching {
            runBlocking {
                val dummykode = Sakstemakode.FOR
                service.hentJournalposterForSakstema(dummyUser, dummykode)
            }
        }

        result.isFailure `should be equal to` true
        result.exceptionOrNull() `should be instance of` CommunicationException::class
    }

}
