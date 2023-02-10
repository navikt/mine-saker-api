package no.nav.personbruker.minesaker.api.saf.journalposter

import no.nav.dokument.saf.selvbetjening.generated.dto.HENT_JOURNALPOSTER
import no.nav.personbruker.minesaker.api.saf.GraphQLRequest
import no.nav.personbruker.minesaker.api.domain.Sakstemakode
import no.nav.personbruker.minesaker.api.saf.compactJson

class JournalposterRequest(override val variables: Variables) : GraphQLRequest {

    override val query: String get() = queryString

    companion object {
        val queryString = compactJson(HENT_JOURNALPOSTER)

        fun create(ident: String, tema: Sakstemakode) = JournalposterRequest(
            Variables(ident, tema.name)
        )
    }
}

data class Variables(
    val ident: String,
    val temaetSomSkalHentes: String
)
