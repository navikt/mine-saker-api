package no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter

object AvsenderMottakerObjectMother {

    fun giveMePersonSomAvsender(
        ident: String? = "123",
        idType: HentJournalposter.AvsenderMottakerIdType? = HentJournalposter.AvsenderMottakerIdType.FNR
    ) =
        HentJournalposter.AvsenderMottaker(ident, idType)

}
