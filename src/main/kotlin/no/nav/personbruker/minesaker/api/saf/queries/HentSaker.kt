package no.nav.personbruker.minesaker.api.saf.queries

import no.nav.personbruker.minesaker.api.saf.GraphQLRequest

class HentSaker(override val variables: Map<String, Any>) : GraphQLRequest {

    override val query: String
        get() = """
            query(${"$"}ident : String!) {
              dokumentoversiktSelvbetjening(ident: ${"$"}ident, tema: []) {
                tema {
                  navn
                  kode
                }
              }
            }
        """.compactJson()

    companion object {
        fun createRequest(ident: String): HentSaker {
            return HentSaker(
                mapOf(
                    "ident" to ident
                )
            )
        }
    }

}
