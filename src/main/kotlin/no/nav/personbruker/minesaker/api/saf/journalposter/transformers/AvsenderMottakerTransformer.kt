package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.common.exception.MissingFieldException
import no.nav.personbruker.minesaker.api.saf.domain.AvsenderMottaker
import no.nav.personbruker.minesaker.api.saf.domain.ID

fun HentJournalposter.AvsenderMottaker.toInternal(identInnloggetBruker: ID) = AvsenderMottaker(
    erSelvAvsender(identInnloggetBruker),
    type?.toInternal() ?: throw MissingFieldException("avsenderMottakerIdType")
)

fun HentJournalposter.AvsenderMottaker.erSelvAvsender(identInnloggetBruker: ID): Boolean {
    var erSelvAvsender = false
    if (avsenderMottakerErEnPrivatperson()) {
        erSelvAvsender = id.equals(identInnloggetBruker.value)
    }
    return erSelvAvsender
}

private fun HentJournalposter.AvsenderMottaker.avsenderMottakerErEnPrivatperson() =
    type?.let { type -> type == HentJournalposter.AvsenderMottakerIdType.FNR } == true
