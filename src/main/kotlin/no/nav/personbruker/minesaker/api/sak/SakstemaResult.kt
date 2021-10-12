package no.nav.personbruker.minesaker.api.sak

import io.ktor.http.*
import no.nav.personbruker.minesaker.api.domain.ForenkletSakstema
import no.nav.personbruker.minesaker.api.domain.Sakstemakode
import no.nav.personbruker.minesaker.api.domain.SistEndredeSakstemaer

data class SakstemaResult(
    private val results: List<ForenkletSakstema>,
    private val errors: List<Kildetype> = emptyList()
) {

    constructor(errors: List<Kildetype>) : this(emptyList(), errors)

    operator fun plus(result: SakstemaResult): SakstemaResult =
        SakstemaResult(this.results + result.results, this.errors + result.errors)

    fun resultsSorted() = mutableListOf<ForenkletSakstema>().apply {
        addAll(results)
        sortByDescending { r -> r.sistEndret }
    }

    fun recentlyModifiedSakstemaResults(): SistEndredeSakstemaer {
        val sortedResults = resultsSorted()
        return SistEndredeSakstemaer(
            sistEndret(sortedResults),
            hentSistEndretForDagpenger(sortedResults)
        )
    }

    private fun sistEndret(sortedResults : List<ForenkletSakstema>): List<ForenkletSakstema> {
        return if (moreThanTwoResults()) {
            sortedResults.subList(0, 2)
        } else {
            sortedResults
        }
    }

    private fun hentSistEndretForDagpenger(sortedResults: MutableList<ForenkletSakstema>) =
        sortedResults.find { s -> s.kode == Sakstemakode.DAG }?.sistEndret

    private fun moreThanTwoResults() = results.size > 2

    fun hasErrors() = errors.isNotEmpty()
    fun errors() = mutableListOf<Kildetype>().apply { addAll(errors) }

    fun determineHttpCode(): HttpStatusCode {
        return when {
            hasPartialResult() -> HttpStatusCode.PartialContent
            allSourcesFailed() -> HttpStatusCode.ServiceUnavailable
            else -> HttpStatusCode.OK
        }
    }

    private fun hasPartialResult(): Boolean = errors.size == 1
    private fun allSourcesFailed(): Boolean = errors.size == Kildetype.values().size

}

enum class Kildetype { SAF, DIGISOS }
