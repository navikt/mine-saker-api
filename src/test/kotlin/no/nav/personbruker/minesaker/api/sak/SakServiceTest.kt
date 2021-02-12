package no.nav.personbruker.minesaker.api.sak

import io.mockk.*
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.minesaker.api.common.AuthenticatedUserObjectMother
import no.nav.personbruker.minesaker.api.common.exception.SafException
import no.nav.personbruker.minesaker.api.common.sak.SakService
import no.nav.personbruker.minesaker.api.saf.SafConsumer
import no.nav.personbruker.minesaker.api.saf.queries.HentKonkretSakstema
import no.nav.personbruker.minesaker.api.saf.queries.HentSaker
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

        val parameterSendtVidere = slot<HentSaker>()

        runBlocking {
            service.hentSaker(dummyUser)
        }

        coVerify(exactly = 1) { consumer.hentSaker(capture(parameterSendtVidere)) }

        parameterSendtVidere.captured `should be instance of` HentSaker::class

        confirmVerified(consumer)
    }

    @Test
    fun `Feil som oppstaar ved henting av alle sakstemaer skal kastes videre`() {
        val expectedException = SafException("Simulert feil i en test")

        val consumer = mockk<SafConsumer>(relaxed = true)
        val service = SakService(consumer)

        coEvery {
            consumer.hentSaker(any())
        } throws expectedException

        val result = runCatching {
            runBlocking {
                service.hentSaker(dummyUser)
            }
        }

        result.isFailure `should be equal to` true
        result.exceptionOrNull() `should be instance of` SafException::class
    }

    @Test
    fun `Skal hente alle data for et konkret sakstema`() {
        val expectedSakstemakode = "FOR"

        val consumer = mockk<SafConsumer>(relaxed = true)
        val service = SakService(consumer)

        val parameterSendtVidere = slot<HentKonkretSakstema>()

        runBlocking {
            service.hentSakstema(dummyUser, expectedSakstemakode)
        }

        coVerify(exactly = 1) { consumer.hentKonkretSakstema(capture(parameterSendtVidere)) }

        parameterSendtVidere.captured `should be instance of` HentKonkretSakstema::class
        parameterSendtVidere.captured.variables.entries.toString() `should contain` expectedSakstemakode

        confirmVerified(consumer)
    }

    @Test
    fun `Feil som oppstaar ved henting av et konkret sakstema skal kastes videre`() {
        val expectedException = SafException("Simulert feil i en test")

        val consumer = mockk<SafConsumer>(relaxed = true)
        val service = SakService(consumer)

        coEvery {
            consumer.hentKonkretSakstema(any())
        } throws expectedException

        val result = runCatching {
            runBlocking {
                service.hentSakstema(dummyUser, "dummykode")
            }
        }

        result.isFailure `should be equal to` true
        result.exceptionOrNull() `should be instance of` SafException::class
    }

}
