package no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter

object AvsenderMottakerObjectMother {

    fun giveMePerson(
        ident: String = "123",
        idType: HentJournalposter.AvsenderMottakerIdType = HentJournalposter.AvsenderMottakerIdType.FNR
    ) =
        HentJournalposter.AvsenderMottaker(ident, idType)

    fun giveMeOrganisasjon(
        ident: String = "987654",
        idType: HentJournalposter.AvsenderMottakerIdType = HentJournalposter.AvsenderMottakerIdType.ORGNR
    ) =
        HentJournalposter.AvsenderMottaker(ident, idType)

}
