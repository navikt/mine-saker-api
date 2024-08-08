package no.nav.tms.minesaker.api.saf.journalposter

import no.nav.dokument.saf.selvbetjening.generated.dto.ALLE_JOURNALPOSTER
import no.nav.dokument.saf.selvbetjening.generated.dto.HENT_JOURNALPOSTER
import no.nav.tms.minesaker.api.saf.GraphQLRequest
import no.nav.tms.minesaker.api.domain.Sakstemakode
import no.nav.tms.minesaker.api.saf.compactJson

class JournalposterRequest(override val variables: JournalposterRequestVariables) : GraphQLRequest {

    override val query: String get() = queryString

    companion object {
        val queryString = compactJson(HENT_JOURNALPOSTER)

        fun create(ident: String, tema: Sakstemakode) = JournalposterRequest(
            JournalposterRequestVariables(ident, tema.name)
        )
    }
}

data class JournalposterRequestVariables(
    val ident: String,
    val temaetSomSkalHentes: String
)

class AlleJournalposterRequest(override val variables: AlleJournalposterRequestVariables) : GraphQLRequest {

    override val query: String get() = queryString

    companion object {
        val queryString = compactJson(ALLE_JOURNALPOSTER)

        fun create(ident: String) = AlleJournalposterRequest(
            AlleJournalposterRequestVariables(ident)
        )
    }
}

data class AlleJournalposterRequestVariables(
    val ident: String
)
