package no.nav.personbruker.minesaker.api.saf.sakstemaer

import no.nav.dokument.saf.selvbetjening.generated.dto.HENT_SAKSTEMAER
import no.nav.personbruker.minesaker.api.saf.GraphQLRequest
import no.nav.personbruker.minesaker.api.saf.domain.Fodselsnummer

class SakstemaerRequest(override val variables: Map<String, Any>) : GraphQLRequest {

    override val query: String
        get() = HENT_SAKSTEMAER.compactJson()

    companion object {
        fun create(ident: Fodselsnummer): SakstemaerRequest {
            return SakstemaerRequest(
                mapOf(
                    "ident" to ident.value
                )
            )
        }
    }

}
