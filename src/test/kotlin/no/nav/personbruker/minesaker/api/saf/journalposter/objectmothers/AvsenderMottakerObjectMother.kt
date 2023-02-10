package no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers

import no.nav.personbruker.minesaker.api.saf.journalposter.transformers.GraphQLAvsenderMottaker
import no.nav.personbruker.minesaker.api.saf.journalposter.transformers.GraphQLAvsenderMottakerIdType

object AvsenderMottakerObjectMother {

    fun giveMePerson(
        ident: String = "123",
        idType: GraphQLAvsenderMottakerIdType = GraphQLAvsenderMottakerIdType.FNR
    ) =
        GraphQLAvsenderMottaker(ident, idType)

    fun giveMeOrganisasjon(
        ident: String = "987654",
        idType: GraphQLAvsenderMottakerIdType = GraphQLAvsenderMottakerIdType.ORGNR
    ) =
        GraphQLAvsenderMottaker(ident, idType)

}
