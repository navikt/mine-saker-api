package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.saf.domain.Datotype

fun HentJournalposter.Datotype.toInternal(): Datotype {
    return when (this) {
        HentJournalposter.Datotype.DATO_OPPRETTET -> Datotype.OPPRETTET
        HentJournalposter.Datotype.DATO_REGISTRERT -> Datotype.REGISTRERT
        HentJournalposter.Datotype.DATO_EKSPEDERT -> Datotype.EKSPEDERT
        HentJournalposter.Datotype.DATO_AVS_RETUR -> Datotype.AV_RETUR
        HentJournalposter.Datotype.DATO_DOKUMENT -> Datotype.DOKUMENT
        HentJournalposter.Datotype.DATO_JOURNALFOERT -> Datotype.JOURNALFORT
        HentJournalposter.Datotype.DATO_SENDT_PRINT -> Datotype.SENDT_PRINT
        HentJournalposter.Datotype.__UNKNOWN_VALUE -> Datotype.UKJENT
    }
}
