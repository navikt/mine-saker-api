package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.common.exception.MissingFieldException
import no.nav.personbruker.minesaker.api.saf.domain.Fodselsnummer
import no.nav.personbruker.minesaker.api.saf.domain.Journalpost
import no.nav.personbruker.minesaker.api.saf.domain.JournalpostId
import no.nav.personbruker.minesaker.api.saf.domain.Tittel

fun HentJournalposter.Journalpost.toInternal(innloggetBruker: Fodselsnummer) = Journalpost(
    Tittel(tittel ?: throw MissingFieldException("tittel")),
    JournalpostId(journalpostId),
    journalposttype?.toInternal() ?: throw MissingFieldException("journalposttype"),
    avsenderMottaker?.toInternal(innloggetBruker) ?: throw MissingFieldException("avsenderMottaker"),
    relevanteDatoer.filterNotNull().map { external -> external.toInternal() },
    DokumentInfoTransformer.toInternal(dokumenter)
)
