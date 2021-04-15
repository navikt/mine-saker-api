package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.common.exception.TransformationException
import no.nav.personbruker.minesaker.api.saf.domain.*

fun HentJournalposter.Journalpost.toInternal(innloggetBruker: Fodselsnummer): Journalpost {
    val relevanteDatoer = relevanteDatoer.filterNotNull().map { external -> external.toInternal() }
    return Journalpost(
        Tittel(tittel ?: "Uten tittel"),
        JournalpostId(journalpostId),
        journalposttype?.toInternal() ?: throw TransformationException.withMissingFieldName("journalposttype"),
        avsenderMottaker?.toInternal(innloggetBruker) ?: throw TransformationException.withMissingFieldName("avsenderMottaker"),
        relevanteDatoer.plukkUtNyesteDato(),
        DokumentInfoTransformer.toInternal(dokumenter)
    )
}
