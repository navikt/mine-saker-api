package no.nav.personbruker.minesaker.api.saf.requests

import no.nav.dokument.saf.selvbetjening.generated.dto.HENT_JOURNALPOSTER
import no.nav.personbruker.minesaker.api.saf.GraphQLRequest

class JournalposterRequest(override val variables: Map<String, Any>) : GraphQLRequest {

    override val query: String
        get() = HENT_JOURNALPOSTER.compactJson()

    companion object {
        fun create(ident: String, temaSomSkalHentes: String): JournalposterRequest {
            return JournalposterRequest(
                mapOf(
                    "ident" to ident,
                    "temaetSomSkalHentes" to temaSomSkalHentes,
                )
            )
        }
    }

}
