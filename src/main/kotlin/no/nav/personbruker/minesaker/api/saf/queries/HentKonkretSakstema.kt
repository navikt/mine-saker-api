package no.nav.personbruker.minesaker.api.saf.queries

import no.nav.personbruker.minesaker.api.saf.GraphQLRequest

class HentKonkretSakstema(override val variables: Map<String, Any>) : GraphQLRequest {

    override val query: String
        get() = """
            query(${"$"}ident : String!, ${"$"}temaetSomSkalHentes : Tema) {
              dokumentoversiktSelvbetjening(ident: ${"$"}ident, tema: [${"$"}temaetSomSkalHentes]) {
                tema {
                  navn
                  kode
                }
              }
            }
        """.compactJson()

    companion object {
        fun createRequest(ident: String, temaSomSkalHentes: String): HentKonkretSakstema {
            return HentKonkretSakstema(
                mapOf(
                    "ident" to ident,
                    "temaetSomSkalHentes" to temaSomSkalHentes,
                )
            )
        }
    }

}
