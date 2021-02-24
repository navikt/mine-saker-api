package no.nav.personbruker.minesaker.api.saf.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.saf.domain.AvsenderMottakerType

object AvsenderMottakerTypeTransformer {

    fun toInternal(external : HentJournalposter.AvsenderMottakerIdType): AvsenderMottakerType {
        return when(external) {
            HentJournalposter.AvsenderMottakerIdType.FNR -> AvsenderMottakerType.PERSON
            HentJournalposter.AvsenderMottakerIdType.HPRNR -> AvsenderMottakerType.HELSEPERSONELL
            HentJournalposter.AvsenderMottakerIdType.ORGNR -> AvsenderMottakerType.ORGANISASJON
            HentJournalposter.AvsenderMottakerIdType.UTL_ORG -> AvsenderMottakerType.ORGANISASJON
            HentJournalposter.AvsenderMottakerIdType.NULL -> AvsenderMottakerType.UKJENT
            HentJournalposter.AvsenderMottakerIdType.UKJENT -> AvsenderMottakerType.UKJENT
            HentJournalposter.AvsenderMottakerIdType.__UNKNOWN_VALUE -> AvsenderMottakerType.UKJENT
        }
    }

}
