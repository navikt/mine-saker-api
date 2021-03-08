package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.saf.domain.AvsenderMottakerType

fun HentJournalposter.AvsenderMottakerIdType.toInternal(): AvsenderMottakerType {
    return when (this) {
        HentJournalposter.AvsenderMottakerIdType.FNR -> AvsenderMottakerType.PERSON
        HentJournalposter.AvsenderMottakerIdType.HPRNR -> AvsenderMottakerType.HELSEPERSONELL
        HentJournalposter.AvsenderMottakerIdType.ORGNR -> AvsenderMottakerType.ORGANISASJON
        HentJournalposter.AvsenderMottakerIdType.UTL_ORG -> AvsenderMottakerType.ORGANISASJON
        HentJournalposter.AvsenderMottakerIdType.NULL -> AvsenderMottakerType.UKJENT
        HentJournalposter.AvsenderMottakerIdType.UKJENT -> AvsenderMottakerType.UKJENT
        HentJournalposter.AvsenderMottakerIdType.__UNKNOWN_VALUE -> AvsenderMottakerType.UKJENT
    }
}
