package no.nav.personbruker.minesaker.api.saf.domain

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter

object AvsenderMottakerObjectMother {

    fun giveMePersonSomAvsender(ident: String) =
        HentJournalposter.AvsenderMottaker(ident, HentJournalposter.AvsenderMottakerIdType.FNR)

    fun giveMeAvsenderUtenType() =
        HentJournalposter.AvsenderMottaker("123", null)

}
