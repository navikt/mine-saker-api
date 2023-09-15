package no.nav.personbruker.minesaker.api.sak

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.minesaker.api.common.IdportenTestUser
import no.nav.personbruker.minesaker.api.exception.CommunicationException
import no.nav.personbruker.minesaker.api.digisos.DigiSosConsumer
import no.nav.personbruker.minesaker.api.domain.Sakstemakode
import no.nav.personbruker.minesaker.api.saf.SafConsumer
import no.nav.personbruker.minesaker.api.config.TokendingsExchange
import no.nav.personbruker.minesaker.api.saf.journalposter.JournalposterRequest
import no.nav.personbruker.minesaker.api.saf.sakstemaer.SakstemaerRequest
import org.junit.jupiter.api.Test

internal class SakServiceTest {

    private val dummyIdportenUser = IdportenTestUser.createIdportenUser()
    private val safDummyToken = "saf<access_token>"
    private val tokendingsExchange = mockk<TokendingsExchange>().also {
        coEvery { it.safToken(any()) } returns safDummyToken
        coEvery { it.digisosToken(any()) } returns "digisos <access-token>"
    }

    @Test
    fun `Skal hente alle sakstemaer for en konkret bruker`() {
        val safConsumer = mockk<SafConsumer>()
        val digiSosConsumer = mockk<DigiSosConsumer>()
        val service = SakService(safConsumer, tokendingsExchange, digiSosConsumer)

        val parameterSendtVidere = slot<SakstemaerRequest>()

        coEvery { safConsumer.hentSakstemaer(any(), any()) } returns SakstemaResultTestData.safResults()
        coEvery { digiSosConsumer.hentSakstemaer(any()) } returns SakstemaResultTestData.createDigiSosResults()

        runBlocking {
            service.hentSakstemaer(dummyIdportenUser, representert = null)
        }

        coVerify(exactly = 1) { safConsumer.hentSakstemaer(capture(parameterSendtVidere), any()) }
        coVerify(exactly = 1) { digiSosConsumer.hentSakstemaer(any()) }

        parameterSendtVidere.captured.shouldBeInstanceOf<SakstemaerRequest>()

        confirmVerified(safConsumer)
        confirmVerified(digiSosConsumer)
    }

    @Test
    fun `Hvis en kilde feiler, returner data fra kilden som svarte og send med info om kilden som feilet`() {
        val safConsumer = mockk<SafConsumer>()
        val digiSosConsumer = mockk<DigiSosConsumer>()
        val service = SakService(safConsumer, tokendingsExchange, digiSosConsumer)

        coEvery { safConsumer.hentSakstemaer(any(), any()) } returns SakstemaResultTestData.safResults()
        coEvery { digiSosConsumer.hentSakstemaer(any()) } returns SakstemaResultTestData.createDigiSosError()

        val result = runBlocking {
            service.hentSakstemaer(dummyIdportenUser, representert = null)
        }

        result.hasErrors() shouldBe true
        result.errors() shouldContain Kildetype.DIGISOS
        result.resultsSorted().shouldNotBeEmpty()
    }

    @Test
    fun `Skal hente alle journalposter for et konkret sakstema`() {
        val expectedSakstemakode = Sakstemakode.FOR

        val safConsumer = mockk<SafConsumer>(relaxed = true)
        val digiSosConsumer = mockk<DigiSosConsumer>()
        val service = SakService(safConsumer, tokendingsExchange, digiSosConsumer)

        val parameterSendtVidere = slot<JournalposterRequest>()

        runBlocking {
            service.hentJournalposterForSakstema(dummyIdportenUser, expectedSakstemakode)
        }

        coVerify(exactly = 1) {
            safConsumer.hentJournalposter(
                dummyIdportenUser.ident,
                capture(parameterSendtVidere),
                safDummyToken
            )
        }

        parameterSendtVidere.captured.shouldBeInstanceOf<JournalposterRequest>()
        parameterSendtVidere.captured.variables.temaetSomSkalHentes shouldContain expectedSakstemakode.toString()

        confirmVerified(safConsumer)
    }

    @Test
    fun `Feil som oppstaar, ved henting av journalposter for et konkret sakstema, skal kastes videre`() {
        val expectedException = CommunicationException("Simulert feil i en test")

        val safConsumer = mockk<SafConsumer>(relaxed = true)
        val digiSosConsumer = mockk<DigiSosConsumer>()
        val service = SakService(safConsumer, tokendingsExchange, digiSosConsumer)

        coEvery {
            safConsumer.hentJournalposter(dummyIdportenUser.ident, any(), any())
        } throws expectedException

        val result = runCatching {
            runBlocking {
                val dummykode = Sakstemakode.FOR
                service.hentJournalposterForSakstema(dummyIdportenUser,dummykode)
            }
        }

        result.isFailure shouldBe true
        result.exceptionOrNull().shouldBeInstanceOf<CommunicationException>()
    }
}
