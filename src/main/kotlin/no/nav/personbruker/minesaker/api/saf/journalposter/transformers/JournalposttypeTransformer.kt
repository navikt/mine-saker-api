package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.common.exception.TransformationException
import no.nav.personbruker.minesaker.api.saf.domain.Journalposttype

fun HentJournalposter.Journalposttype.toInternal(): Journalposttype {
    return when (this) {
        HentJournalposter.Journalposttype.I -> Journalposttype.INNGAAENDE
        HentJournalposter.Journalposttype.U -> Journalposttype.UTGAAENDE
        HentJournalposter.Journalposttype.N -> Journalposttype.NOTAT
        HentJournalposter.Journalposttype.__UNKNOWN_VALUE -> throw buildException()
    }
}

private fun buildException(): TransformationException {
    val message = "Mottok ukjent verdi for feltet 'journalposttype'."
    return TransformationException(message, TransformationException.ErrorType.UNKNOWN_VALUE)
}
