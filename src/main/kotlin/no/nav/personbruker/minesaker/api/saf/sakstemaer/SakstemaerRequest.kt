package no.nav.personbruker.minesaker.api.saf.sakstemaer

import no.nav.dokument.saf.selvbetjening.generated.dto.HENT_SAKSTEMAER
import no.nav.personbruker.minesaker.api.saf.GraphQLRequest
import no.nav.personbruker.minesaker.api.saf.compactJson

class SakstemaerRequest(override val variables: Variables) : GraphQLRequest {

    override val query get() = queryString

    companion object {
        val queryString = compactJson(HENT_SAKSTEMAER)

        fun create(ident: String) = SakstemaerRequest (
            Variables(ident)
        )
    }
}

data class Variables(
    val ident: String
)
