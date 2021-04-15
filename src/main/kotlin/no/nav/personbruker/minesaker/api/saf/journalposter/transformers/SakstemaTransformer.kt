package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.common.exception.TransformationException
import no.nav.personbruker.minesaker.api.saf.domain.Fodselsnummer
import no.nav.personbruker.minesaker.api.saf.domain.Navn
import no.nav.personbruker.minesaker.api.saf.domain.Sakstema

fun HentJournalposter.Sakstema.toInternal(innloggetBruker: Fodselsnummer) = Sakstema(
    Navn(navn ?: throw TransformationException.withMissingFieldName("navn")),
    kode?.toInternalSaktemakode() ?: throw TransformationException.withMissingFieldName("kode"),
    journalposter.toInternal(innloggetBruker)
)
