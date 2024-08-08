package no.nav.tms.minesaker.api.sak

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import no.nav.tms.minesaker.api.saf.sakstemaer.Kildetype
import no.nav.tms.minesaker.api.saf.sakstemaer.SakstemaResult
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

internal class SakstemaResultTest {

    @Test
    fun `Skal returnere http-kode ok hvis alle kilder svarer`() {
        val result = SakstemaResultTestData.safResults()

        result.determineHttpCode() shouldBe HttpStatusCode.OK
    }

    @Test
    fun `Skal returnere http-kode partial result hvis en kilde ikke svarer`() {
        val result = SakstemaResultTestData.createDigiSosError()

        result.determineHttpCode() shouldBe HttpStatusCode.OK
    }

    @Test
    fun `Skal returnere http-kode service unavailable hvis ingen kilder svarer`() {
        val result = SakstemaResult(emptyList(), listOf(Kildetype.SAF, Kildetype.DIGISOS))
        result.determineHttpCode() shouldBe HttpStatusCode.ServiceUnavailable
    }

    @Test
    fun `Skal returnere resultatene sortert etter siste endret`() {
        val nyeste = ForenkletSakstemaTestData.dagpengerResult(ZonedDateTime.now().minusDays(5))
        val midterste = ForenkletSakstemaTestData.pensjonResult(ZonedDateTime.now().minusDays(60))
        val eldste = ForenkletSakstemaTestData.okonomiskSosialhjelpResult(ZonedDateTime.now().minusDays(100))
        val unsortedResults = listOf(
            eldste,
            nyeste,
            midterste
        )

        val sakstemaResult = SakstemaResult(unsortedResults)

        val sortedResults = sakstemaResult.resultsSorted()
        sortedResults.size shouldBe unsortedResults.size
        sortedResults[0] shouldBe nyeste
        sortedResults[1] shouldBe midterste
        sortedResults[2] shouldBe eldste
    }

    @Test
    fun `Skal returnere de to siste endrede sakene, og dato for siste endring for dagpenger`() {
        val nyeste = ForenkletSakstemaTestData.okonomiskSosialhjelpResult(ZonedDateTime.now().minusDays(5))
        val midterste = ForenkletSakstemaTestData.pensjonResult(ZonedDateTime.now().minusDays(60))
        val forGammelDagpengeSak = ForenkletSakstemaTestData.dagpengerResult(ZonedDateTime.now().minusDays(100))
        val unsortedResults = listOf(
            forGammelDagpengeSak,
            nyeste,
            midterste
        )

        val sakstemaResult = SakstemaResult(unsortedResults)

        val lastModified = sakstemaResult.recentlyModifiedSakstemaResults()
        lastModified.sistEndrede.size shouldBe 2
        lastModified.sistEndrede shouldContain nyeste
        lastModified.sistEndrede shouldContain midterste
        lastModified.sistEndrede shouldNotContain forGammelDagpengeSak
        lastModified.dagpengerSistEndret shouldBe forGammelDagpengeSak.sistEndret
    }

    @Test
    fun `Skal returnere de to siste endrede sakene, og dato for siste endring for dagpenger selv om dagpenger er en av de to siste`() {
        val nyeste = ForenkletSakstemaTestData.okonomiskSosialhjelpResult(ZonedDateTime.now().minusDays(5))
        val dagpenger = ForenkletSakstemaTestData.dagpengerResult(ZonedDateTime.now().minusDays(100))
        val unsortedResults = listOf(
            dagpenger,
            nyeste
        )

        val sakstemaResult = SakstemaResult(unsortedResults)

        val lastModified = sakstemaResult.recentlyModifiedSakstemaResults()
        lastModified.sistEndrede.size shouldBe 2
        lastModified.sistEndrede shouldContain nyeste
        lastModified.sistEndrede shouldContain dagpenger
        lastModified.dagpengerSistEndret shouldBe dagpenger.sistEndret
    }

    @Test
    fun `Skal returnere de to siste endrede sakene, og tom dato for dagpenger (hvis bruker ikke har hatt dagpenger)`() {
        val nyeste = ForenkletSakstemaTestData.okonomiskSosialhjelpResult(ZonedDateTime.now().minusDays(5))
        val midterste = ForenkletSakstemaTestData.pensjonResult(ZonedDateTime.now().minusDays(60))
        val unsortedResults = listOf(
            nyeste,
            midterste
        )

        val sakstemaResult = SakstemaResult(unsortedResults)

        val lastModified = sakstemaResult.recentlyModifiedSakstemaResults()
        lastModified.sistEndrede.size shouldBe 2
        lastModified.sistEndrede shouldContain nyeste
        lastModified.sistEndrede shouldContain midterste
        lastModified.dagpengerSistEndret.shouldBeNull()
    }

    @Test
    fun `Skal returnere et sakstema, hvis det ikke finnes flere`() {
        val enesteSakstema = ForenkletSakstemaTestData.dagpengerResult(ZonedDateTime.now().minusDays(5))
        val results = listOf(enesteSakstema)

        val sakstemaResult = SakstemaResult(results)

        val newestResult = sakstemaResult.recentlyModifiedSakstemaResults()
        newestResult.sistEndrede.size shouldBe 1
        newestResult.sistEndrede shouldContain enesteSakstema
    }

}
