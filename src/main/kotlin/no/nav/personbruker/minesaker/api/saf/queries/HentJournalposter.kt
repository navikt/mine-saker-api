package no.nav.personbruker.minesaker.api.saf.queries

import no.nav.dokument.saf.selvbetjening.generated.dto.HENT_KONKRET_SAKSTEMA_DTO
import no.nav.personbruker.minesaker.api.saf.GraphQLRequest

class HentJournalposter(override val variables: Map<String, Any>) : GraphQLRequest {

    override val query: String
        get() = HENT_KONKRET_SAKSTEMA_DTO.compactJson()

    companion object {
        fun createRequest(ident: String, temaSomSkalHentes: String): HentJournalposter {
            return HentJournalposter(
                mapOf(
                    "ident" to ident,
                    "temaetSomSkalHentes" to temaSomSkalHentes,
                )
            )
        }
    }

}
