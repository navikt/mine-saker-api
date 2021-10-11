package no.nav.personbruker.minesaker.api.sak

import io.ktor.http.*
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should contain`
import org.amshove.kluent.`should not contain`
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
    fun `Skal returnere de to siste endrede sakene`() {
        val nyeste = ForenkletSakstemaObjectMother.giveMeDagpengerResult(ZonedDateTime.now().minusDays(5))
        val midterste = ForenkletSakstemaObjectMother.giveMePensjonResult(ZonedDateTime.now().minusDays(60))
        val eldste = ForenkletSakstemaObjectMother.giveMeOkonomiskSosialhjelpResult(ZonedDateTime.now().minusDays(100))
        val unsortedResults = listOf(
            eldste,
            nyeste,
            midterste
        )

        val sakstemaResult = SakstemaResult(unsortedResults)

        val twoNewestResults = sakstemaResult.theTwoMostRecentlyModifiedResults()
        twoNewestResults.size `should be equal to` 2
        twoNewestResults `should contain` nyeste
        twoNewestResults `should contain` midterste
        twoNewestResults `should not contain` eldste
    }

    @Test
    fun `Skal returnere et sakstema, hvis det ikke finnes flere`() {
        val enesteSakstema = ForenkletSakstemaObjectMother.giveMeDagpengerResult(ZonedDateTime.now().minusDays(5))
        val results = listOf(enesteSakstema)

        val sakstemaResult = SakstemaResult(results)

        val newestResult = sakstemaResult.theTwoMostRecentlyModifiedResults()
        newestResult.size `should be equal to` 1
        newestResult `should contain` enesteSakstema
    }

}
