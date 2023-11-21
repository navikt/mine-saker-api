package no.nav.tms.minesaker.api.saf.journalposter.transformers

import no.nav.tms.minesaker.api.domain.Dokumentkilde

fun GraphQLAvsenderMottaker.toInternal(innloggetBruker: String) = Dokumentkilde(
    innloggetBrukerErAvsender(innloggetBruker),
    type.toInternal()
)

fun GraphQLAvsenderMottaker.innloggetBrukerErAvsender(innloggetBruker: String): Boolean {
    return if (avsenderMottakerErEnPrivatperson()) {
        id == innloggetBruker
    } else {
        false
    }
}

private fun GraphQLAvsenderMottaker.avsenderMottakerErEnPrivatperson() =
    type == GraphQLAvsenderMottakerIdType.FNR
