package no.nav.personbruker.minesaker.api.sak

import io.ktor.http.*
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should contain`
import org.amshove.kluent.`should not contain`
import org.amshove.kluent.shouldBeNull
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

internal class SakstemaResultTest {

    @Test
    fun `Skal returnere http-kode ok hvis alle kilder svarer`() {
        val result = SakstemaResultObjectMother.createSafResults()

        result.determineHttpCode() `should be equal to` HttpStatusCode.OK
    }

    @Test
    fun `Skal returnere http-kode partial result hvis en kilde ikke svarer`() {
        val result = SakstemaResultObjectMother.createResultWithOneError()

        result.determineHttpCode() `should be equal to` HttpStatusCode.PartialContent
    }

    @Test
    fun `Skal returnere http-kode service unavailable hvis ingen kilder svarer`() {
        val result = SakstemaResultObjectMother.createResultWithTwoErrors()

        result.determineHttpCode() `should be equal to` HttpStatusCode.ServiceUnavailable
    }

    @Test
    fun `Skal returnere resultatene sortert etter siste endret`() {
        val nyeste = ForenkletSakstemaObjectMother.giveMeDagpengerResult(ZonedDateTime.now().minusDays(5))
        val midterste = ForenkletSakstemaObjectMother.giveMePensjonResult(ZonedDateTime.now().minusDays(60))
        val eldste = ForenkletSakstemaObjectMother.giveMeOkonomiskSosialhjelpResult(ZonedDateTime.now().minusDays(100))
        val unsortedResults = listOf(
            eldste,
            nyeste,
            midterste
        )

        val sakstemaResult = SakstemaResult(unsortedResults)

        val sortedResults = sakstemaResult.resultsSorted()
        sortedResults.size `should be equal to` unsortedResults.size
        sortedResults[0] `should be equal to` nyeste
        sortedResults[1] `should be equal to` midterste
        sortedResults[2] `should be equal to` eldste
    }

    @Test
    fun `Skal returnere de to siste endrede sakene, og dato for siste endring for dagpenger`() {
        val nyeste = ForenkletSakstemaObjectMother.giveMeOkonomiskSosialhjelpResult(ZonedDateTime.now().minusDays(5))
        val midterste = ForenkletSakstemaObjectMother.giveMePensjonResult(ZonedDateTime.now().minusDays(60))
        val forGammelDagpengeSak = ForenkletSakstemaObjectMother.giveMeDagpengerResult(ZonedDateTime.now().minusDays(100))
        val unsortedResults = listOf(
            forGammelDagpengeSak,
            nyeste,
            midterste
        )

        val sakstemaResult = SakstemaResult(unsortedResults)

        val lastModified = sakstemaResult.recentlyModifiedSakstemaResults()
        lastModified.sistEndrede.size `should be equal to` 2
        lastModified.sistEndrede `should contain` nyeste
        lastModified.sistEndrede `should contain` midterste
        lastModified.sistEndrede `should not contain` forGammelDagpengeSak
        lastModified.dagpengerSistEndret `should be equal to` forGammelDagpengeSak.sistEndret
    }

    @Test
    fun `Skal returnere de to siste endrede sakene, og dato for siste endring for dagpenger selv om dagpenger er en av de to siste`() {
        val nyeste = ForenkletSakstemaObjectMother.giveMeOkonomiskSosialhjelpResult(ZonedDateTime.now().minusDays(5))
        val dagpenger = ForenkletSakstemaObjectMother.giveMeDagpengerResult(ZonedDateTime.now().minusDays(100))
        val unsortedResults = listOf(
            dagpenger,
            nyeste
        )

        val sakstemaResult = SakstemaResult(unsortedResults)

        val lastModified = sakstemaResult.recentlyModifiedSakstemaResults()
        lastModified.sistEndrede.size `should be equal to` 2
        lastModified.sistEndrede `should contain` nyeste
        lastModified.sistEndrede `should contain` dagpenger
        lastModified.dagpengerSistEndret `should be equal to` dagpenger.sistEndret
    }

    @Test
    fun `Skal returnere de to siste endrede sakene, og tom dato for dagpenger (hvis bruker ikke har hatt dagpenger)`() {
        val nyeste = ForenkletSakstemaObjectMother.giveMeOkonomiskSosialhjelpResult(ZonedDateTime.now().minusDays(5))
        val midterste = ForenkletSakstemaObjectMother.giveMePensjonResult(ZonedDateTime.now().minusDays(60))
        val unsortedResults = listOf(
            nyeste,
            midterste
        )

        val sakstemaResult = SakstemaResult(unsortedResults)

        val lastModified = sakstemaResult.recentlyModifiedSakstemaResults()
        lastModified.sistEndrede.size `should be equal to` 2
        lastModified.sistEndrede `should contain` nyeste
        lastModified.sistEndrede `should contain` midterste
        lastModified.dagpengerSistEndret.shouldBeNull()
    }

    @Test
    fun `Skal returnere et sakstema, hvis det ikke finnes flere`() {
        val enesteSakstema = ForenkletSakstemaObjectMother.giveMeDagpengerResult(ZonedDateTime.now().minusDays(5))
        val results = listOf(enesteSakstema)

        val sakstemaResult = SakstemaResult(results)

        val newestResult = sakstemaResult.recentlyModifiedSakstemaResults()
        newestResult.sistEndrede.size `should be equal to` 1
        newestResult.sistEndrede `should contain` enesteSakstema
    }

}
