package no.nav.tms.minesaker.api.saf.journalposter.transformers

import no.nav.tms.minesaker.api.exception.TransformationException
import no.nav.tms.minesaker.api.domain.Journalposttype

fun GraphQLJournalposttype.toInternal(): Journalposttype {
    return when (this) {
        GraphQLJournalposttype.I -> Journalposttype.INNGAAENDE
        GraphQLJournalposttype.U -> Journalposttype.UTGAAENDE
        GraphQLJournalposttype.N -> Journalposttype.NOTAT
        GraphQLJournalposttype.__UNKNOWN_VALUE -> throw buildException()
    }
}

private fun buildException(): TransformationException {
    val message = "Mottok ukjent verdi for feltet 'journalposttype'."
    return TransformationException(message, TransformationException.ErrorType.UNKNOWN_VALUE)
}
