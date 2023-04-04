package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.personbruker.minesaker.api.exception.TransformationException
import no.nav.personbruker.minesaker.api.domain.Journalpost

fun GraphQLJournalpost.toInternal(innloggetBruker: String) = Journalpost(
        tittel ?: "Uten tittel",
        journalpostId,
        journalposttype.toInternal(),
        avsender?.toInternal(innloggetBruker),
        mottaker?.toInternal(innloggetBruker),
        relevanteDatoer.toInternal(),
        dokumenter?.toInternal() ?: throw TransformationException.withMissingFieldName("dokumenter")
    )

fun List<GraphQLJournalpost?>.toInternal(innloggetBruker: String): List<Journalpost> {
    return filterNotNull().map { external -> external.toInternal(innloggetBruker) }
}
