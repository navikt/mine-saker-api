package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.common.exception.MissingFieldException
import no.nav.personbruker.minesaker.api.saf.domain.AvsenderMottaker
import no.nav.personbruker.minesaker.api.saf.domain.Fodselsnummer

fun HentJournalposter.AvsenderMottaker.toInternal(innloggetBruker: Fodselsnummer) = AvsenderMottaker(
    erSelvAvsender(innloggetBruker),
    type?.toInternal() ?: throw MissingFieldException("avsenderMottakerIdType")
)

fun HentJournalposter.AvsenderMottaker.erSelvAvsender(innloggetBruker: Fodselsnummer): Boolean {
    var erSelvAvsender = false
    if (avsenderMottakerErEnPrivatperson()) {
        erSelvAvsender = id.equals(innloggetBruker.value)
    }
    return erSelvAvsender
}

private fun HentJournalposter.AvsenderMottaker.avsenderMottakerErEnPrivatperson() =
    type?.let { type -> type == HentJournalposter.AvsenderMottakerIdType.FNR } == true
