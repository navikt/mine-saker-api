package no.nav.personbruker.minesaker.api.sak

import io.ktor.http.*
import no.nav.personbruker.minesaker.api.domain.ForenkletSakstema

data class SakstemaResult(
    private val results : List<ForenkletSakstema>,
    private val errors : List<Kildetype> = emptyList(),
    private val cause : Exception? = null
) {

    constructor(errors: List<Kildetype>, cause : Exception) : this(emptyList(), errors, cause)

    operator fun plus(result: SakstemaResult): SakstemaResult =
        SakstemaResult(this.results + result.results, this.errors + result.errors)

    fun results() = mutableListOf<ForenkletSakstema>().apply { addAll(results) }

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
