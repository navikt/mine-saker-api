package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.common.exception.MissingFieldException
import no.nav.personbruker.minesaker.api.saf.domain.AvsenderMottaker
import no.nav.personbruker.minesaker.api.saf.domain.Fodselsnummer

fun HentJournalposter.AvsenderMottaker.toInternal(innloggetBruker: Fodselsnummer) = AvsenderMottaker(
    innloggetBrukerErAvsender(innloggetBruker),
    type?.toInternal() ?: throw MissingFieldException("avsenderMottakerIdType")
)

fun HentJournalposter.AvsenderMottaker.innloggetBrukerErAvsender(innloggetBruker: Fodselsnummer): Boolean {
    var innloggetBrukerErAvsender = false
    if (avsenderMottakerErEnPrivatperson()) {
        innloggetBrukerErAvsender = id.equals(innloggetBruker.value)
    }
    return innloggetBrukerErAvsender
}

private fun HentJournalposter.AvsenderMottaker.avsenderMottakerErEnPrivatperson() =
    type?.let { type -> type == HentJournalposter.AvsenderMottakerIdType.FNR } == true
