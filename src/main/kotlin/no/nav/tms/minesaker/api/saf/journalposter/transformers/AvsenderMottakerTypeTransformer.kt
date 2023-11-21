package no.nav.tms.minesaker.api.saf.journalposter.transformers

import no.nav.tms.minesaker.api.domain.DokumentkildeType

fun GraphQLAvsenderMottakerIdType.toInternal(): DokumentkildeType {
    return when (this) {
        GraphQLAvsenderMottakerIdType.FNR -> DokumentkildeType.PERSON
        GraphQLAvsenderMottakerIdType.HPRNR -> DokumentkildeType.HELSEPERSONELL
        GraphQLAvsenderMottakerIdType.ORGNR -> DokumentkildeType.ORGANISASJON
        GraphQLAvsenderMottakerIdType.UTL_ORG -> DokumentkildeType.ORGANISASJON
        GraphQLAvsenderMottakerIdType.NULL -> DokumentkildeType.UKJENT
        GraphQLAvsenderMottakerIdType.UKJENT -> DokumentkildeType.UKJENT
        GraphQLAvsenderMottakerIdType.__UNKNOWN_VALUE -> DokumentkildeType.UKJENT
    }
}
