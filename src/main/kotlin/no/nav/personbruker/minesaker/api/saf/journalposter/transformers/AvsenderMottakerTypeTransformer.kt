package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.domain.DokumentkildeType

fun HentJournalposter.AvsenderMottakerIdType.toInternal(): DokumentkildeType {
    return when (this) {
        HentJournalposter.AvsenderMottakerIdType.FNR -> DokumentkildeType.PERSON
        HentJournalposter.AvsenderMottakerIdType.HPRNR -> DokumentkildeType.HELSEPERSONELL
        HentJournalposter.AvsenderMottakerIdType.ORGNR -> DokumentkildeType.ORGANISASJON
        HentJournalposter.AvsenderMottakerIdType.UTL_ORG -> DokumentkildeType.ORGANISASJON
        HentJournalposter.AvsenderMottakerIdType.NULL -> DokumentkildeType.UKJENT
        HentJournalposter.AvsenderMottakerIdType.UKJENT -> DokumentkildeType.UKJENT
        HentJournalposter.AvsenderMottakerIdType.__UNKNOWN_VALUE -> DokumentkildeType.UKJENT
    }
}
