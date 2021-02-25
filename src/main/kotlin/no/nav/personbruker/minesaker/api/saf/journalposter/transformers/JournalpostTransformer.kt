package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.common.exception.MissingFieldException
import no.nav.personbruker.minesaker.api.saf.domain.Journalpost

object JournalpostTransformer {

    fun toInternal(externals: List<HentJournalposter.Journalpost?>): List<Journalpost> {
        return externals
            .filterNotNull()
            .map { external -> toInternal(external) }
    }

    fun toInternal(external: HentJournalposter.Journalpost): Journalpost {
        val journalposttype = JournalposttypeTransformer.toInternal(external.journalposttype)
        return Journalpost(
            external.tittel ?: throw MissingFieldException("tittel"),
            external.journalpostId,
            journalposttype,
            AvsenderMottakerTransformer.toInternal(external.avsenderMottaker),
            RelevantDatoTransformer.toInternal(external.relevanteDatoer),
            DokumentInfoTransformer.toInternal(external.dokumenter)
        )
    }

}
