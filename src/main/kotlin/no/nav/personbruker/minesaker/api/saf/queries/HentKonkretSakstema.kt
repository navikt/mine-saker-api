package no.nav.personbruker.minesaker.api.saf.queries

import no.nav.personbruker.minesaker.api.saf.GraphQLRequest

class HentKonkretSakstema(override val variables: Map<String, Any>) : GraphQLRequest {

    override val query: String
        get() = """
            query(${"$"}temaetSomSkalHentes : Tema) {
              dokumentoversiktSelvbetjening(tema: [${"$"}temaetSomSkalHentes]) {
                tema {
                  navn
                  kode
                }
              }
            }
        """.compactJson()

    companion object{
        fun createRequest(temaSomSkalHentes: String): HentKonkretSakstema {
            return HentKonkretSakstema(mapOf("temaetSomSkalHentes" to temaSomSkalHentes))
        }
    }

}
