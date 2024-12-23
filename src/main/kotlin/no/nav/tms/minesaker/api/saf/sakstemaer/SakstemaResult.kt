package no.nav.tms.minesaker.api.saf.sakstemaer

import io.ktor.http.*

data class SakstemaResult(
    private val results: List<ForenkletSakstema>,
    private val errors: List<Kildetype> = emptyList(),
) {

    companion object {
        fun withErrors(errors: List<Kildetype>) = SakstemaResult(emptyList(), errors)
    }

    operator fun plus(result: SakstemaResult): SakstemaResult =
        SakstemaResult(
            results = this.results + result.results,
            errors = this.errors + result.errors,
        )

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

    fun recentlyModified(sakerUrl: String): SakstemaerResponse {
        val sortedResults = resultsSorted()
        return SakstemaerResponse(
            sakstemaer = sistEndret(sortedResults),
            dagpengerSistEndret = hentSistEndretForDagpenger(sortedResults),
            sakerURL = sakerUrl
        )
    }


    private fun sistEndret(sortedResults: List<ForenkletSakstema>): List<ForenkletSakstema> {
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
            allSourcesFailed() -> HttpStatusCode.ServiceUnavailable
            else -> HttpStatusCode.OK
        }
    }

    private fun allSourcesFailed(): Boolean = errors.size == Kildetype.values().size

}

enum class Kildetype { SAF, DIGISOS }


