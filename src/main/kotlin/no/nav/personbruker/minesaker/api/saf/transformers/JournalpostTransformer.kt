package no.nav.personbruker.minesaker.api.saf.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.common.exception.MissingFieldException
import no.nav.personbruker.minesaker.api.saf.domain.Journalpost

object JournalpostTransformer {

    fun toInternal(external: HentJournalposter.Journalpost): Journalpost {
        val journalposttype = JournalposttypeTransformer.toInternal(external.journalposttype)
        return Journalpost(
            external.tittel ?: throw MissingFieldException("tittel"),
            external.journalpostId,
            journalposttype,
            AvsenderMottakerTransformer.toInternal(external.avsenderMottaker),
            RelevantDatoTransformer.toInternal(external.relevanteDatoer, journalposttype),
            DokumentInfoTransformer.toInternal(external.dokumenter)
        )
    }

}
