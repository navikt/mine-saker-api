package no.nav.personbruker.minesaker.api.sak

import io.mockk.*
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.minesaker.api.common.AuthenticatedUserObjectMother
import no.nav.personbruker.minesaker.api.common.exception.SafException
import no.nav.personbruker.minesaker.api.common.sak.SakService
import no.nav.personbruker.minesaker.api.saf.SafConsumer
import no.nav.personbruker.minesaker.api.saf.journalposter.JournalposterRequest
import no.nav.personbruker.minesaker.api.saf.sakstemaer.SakstemaerRequest
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be instance of`
import org.amshove.kluent.`should contain`
import org.junit.jupiter.api.Test

internal class SakServiceTest {

    private val dummyUser = AuthenticatedUserObjectMother.createAuthenticatedUser()

    @Test
    fun `Skal hente alle sakstemaer for en konkret bruker`() {
        val consumer = mockk<SafConsumer>(relaxed = true)
        val service = SakService(consumer)

        val parameterSendtVidere = slot<SakstemaerRequest>()

        runBlocking {
            service.hentSakstemaer(dummyUser)
        }

        coVerify(exactly = 1) { consumer.hentSakstemaer(capture(parameterSendtVidere)) }

        parameterSendtVidere.captured `should be instance of` SakstemaerRequest::class

        confirmVerified(consumer)
    }

    @Test
    fun `Feil som oppstaar, ved henting av alle sakstemaer, skal kastes videre`() {
        val expectedException = SafException("Simulert feil i en test")

        val consumer = mockk<SafConsumer>(relaxed = true)
        val service = SakService(consumer)

        coEvery {
            consumer.hentSakstemaer(any())
        } throws expectedException

        val result = runCatching {
            runBlocking {
                service.hentSakstemaer(dummyUser)
            }
        }

        result.isFailure `should be equal to` true
        result.exceptionOrNull() `should be instance of` SafException::class
    }

    @Test
    fun `Skal hente alle journalposter for et konkret sakstema`() {
        val expectedSakstemakode = "FOR"

        val consumer = mockk<SafConsumer>(relaxed = true)
        val service = SakService(consumer)

        val parameterSendtVidere = slot<JournalposterRequest>()

        runBlocking {
            service.hentJournalposterForSakstema(dummyUser, expectedSakstemakode)
        }

        coVerify(exactly = 1) { consumer.hentJournalposter(capture(parameterSendtVidere)) }

        parameterSendtVidere.captured `should be instance of` JournalposterRequest::class
        parameterSendtVidere.captured.variables.entries.toString() `should contain` expectedSakstemakode

        confirmVerified(consumer)
    }

    @Test
    fun `Feil som oppstaar, ved henting av journalposter for et konkret sakstema, skal kastes videre`() {
        val expectedException = SafException("Simulert feil i en test")

        val consumer = mockk<SafConsumer>(relaxed = true)
        val service = SakService(consumer)

        coEvery {
            consumer.hentJournalposter(any())
        } throws expectedException

        val result = runCatching {
            runBlocking {
                service.hentJournalposterForSakstema(dummyUser, "dummykode")
            }
        }

        result.isFailure `should be equal to` true
        result.exceptionOrNull() `should be instance of` SafException::class
    }

}
