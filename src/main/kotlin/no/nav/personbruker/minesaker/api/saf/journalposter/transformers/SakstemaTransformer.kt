package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.common.exception.MissingFieldException
import no.nav.personbruker.minesaker.api.saf.domain.Navn
import no.nav.personbruker.minesaker.api.saf.domain.Sakstema
import no.nav.personbruker.minesaker.api.saf.domain.toInternalSaktemakode

fun HentJournalposter.Sakstema.toInternal() = Sakstema(
    Navn(navn ?: throw MissingFieldException("navn")),
    kode?.toInternalSaktemakode() ?: throw MissingFieldException("kode"),
    journalposter.filterNotNull().map { external -> external.toInternal() }
)
