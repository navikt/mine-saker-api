package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.common.exception.TransformationException
import no.nav.personbruker.minesaker.api.saf.domain.Fodselsnummer
import no.nav.personbruker.minesaker.api.saf.domain.Journalpost
import no.nav.personbruker.minesaker.api.saf.domain.JournalpostId
import no.nav.personbruker.minesaker.api.saf.domain.Tittel

fun HentJournalposter.Journalpost.toInternal(innloggetBruker: Fodselsnummer): Journalpost {

    return Journalpost(
        Tittel(tittel ?: "Uten tittel"),
        JournalpostId(journalpostId),
        journalposttype?.toInternal() ?: throw TransformationException.withMissingFieldName("journalposttype"),
        avsenderMottaker?.toInternal(innloggetBruker) ?: throw TransformationException.withMissingFieldName("avsenderMottaker"),
        relevanteDatoer.toInternal(),
        DokumentInfoTransformer.toInternal(dokumenter)
    )
}
