package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.common.exception.MissingFieldException
import no.nav.personbruker.minesaker.api.saf.domain.Journalpost

object JournalpostTransformer {

    fun toInternal(externals: List<HentJournalposter.Journalpost?>): List<Journalpost> {
        return externals
            .filterNotNull()
            .map { external -> external.toInternal() }
    }

}

fun HentJournalposter.Journalpost.toInternal() = Journalpost(
    tittel ?: throw MissingFieldException("tittel"),
    journalpostId,
    JournalposttypeTransformer.toInternal(journalposttype),
    AvsenderMottakerTransformer.toInternal(avsenderMottaker),
    RelevantDatoTransformer.toInternal(relevanteDatoer),
    DokumentInfoTransformer.toInternal(dokumenter)
)
