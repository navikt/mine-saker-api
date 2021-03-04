package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.common.exception.MissingFieldException
import no.nav.personbruker.minesaker.api.saf.domain.Journalpost
import no.nav.personbruker.minesaker.api.saf.domain.JournalpostId
import no.nav.personbruker.minesaker.api.saf.domain.Tittel

object JournalpostTransformer {

    fun toInternal(externals: List<HentJournalposter.Journalpost?>): List<Journalpost> {
        return externals
            .filterNotNull()
            .map { external -> external.toInternal() }
    }

}

fun HentJournalposter.Journalpost.toInternal() = Journalpost(
    Tittel(tittel ?: throw MissingFieldException("tittel")),
    JournalpostId(journalpostId),
    journalposttype?.toInternal() ?: throw MissingFieldException("journalposttype"),
    avsenderMottaker?.toInternal() ?: throw MissingFieldException("avsenderMottaker"),
    relevanteDatoer.filterNotNull().map { external -> external.toInternal() },
    DokumentInfoTransformer.toInternal(dokumenter)
)
