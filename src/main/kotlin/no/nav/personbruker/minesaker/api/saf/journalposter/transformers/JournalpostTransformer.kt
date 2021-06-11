package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.common.exception.TransformationException
import no.nav.personbruker.minesaker.api.domain.Fodselsnummer
import no.nav.personbruker.minesaker.api.domain.Journalpost
import no.nav.personbruker.minesaker.api.domain.JournalpostId
import no.nav.personbruker.minesaker.api.domain.Tittel

fun HentJournalposter.Journalpost.toInternal(innloggetBruker: Fodselsnummer) = Journalpost(
        Tittel(tittel ?: "Uten tittel"),
        JournalpostId(journalpostId),
        journalposttype?.toInternal() ?: throw TransformationException.withMissingFieldName("journalposttype"),
        avsender?.toInternal(innloggetBruker) ?: throw TransformationException.withMissingFieldName("avsender"),
        mottaker?.toInternal(innloggetBruker) ?: throw TransformationException.withMissingFieldName("mottaker"),
        relevanteDatoer.toInternal(),
        dokumenter?.toInternal() ?: throw TransformationException.withMissingFieldName("dokumenter")
    )

fun List<HentJournalposter.Journalpost?>.toInternal(innloggetBruker: Fodselsnummer): List<Journalpost> {
    return filterNotNull().map { external -> external.toInternal(innloggetBruker) }
}
