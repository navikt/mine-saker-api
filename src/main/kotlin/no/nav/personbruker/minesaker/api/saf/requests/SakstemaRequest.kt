package no.nav.personbruker.minesaker.api.saf.requests

import no.nav.dokument.saf.selvbetjening.generated.dto.HENT_SAKER_DTO
import no.nav.personbruker.minesaker.api.saf.GraphQLRequest

class SakstemaRequest(override val variables: Map<String, Any>) : GraphQLRequest {

    override val query: String
        get() = HENT_SAKER_DTO.compactJson()

    companion object {
        fun create(ident: String): SakstemaRequest {
            return SakstemaRequest(
                mapOf(
                    "ident" to ident
                )
            )
        }
    }

}
