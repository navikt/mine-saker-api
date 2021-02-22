package no.nav.personbruker.minesaker.api.saf.queries

import no.nav.dokument.saf.selvbetjening.generated.dto.HENT_SAKER_DTO
import no.nav.personbruker.minesaker.api.saf.GraphQLRequest

class HentSakstema(override val variables: Map<String, Any>) : GraphQLRequest {

    override val query: String
        get() = HENT_SAKER_DTO.compactJson()

    companion object {
        fun createRequest(ident: String): HentSakstema {
            return HentSakstema(
                mapOf(
                    "ident" to ident
                )
            )
        }
    }

}
