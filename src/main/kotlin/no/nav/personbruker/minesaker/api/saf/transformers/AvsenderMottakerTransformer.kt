package no.nav.personbruker.minesaker.api.saf.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.common.exception.MissingFieldException
import no.nav.personbruker.minesaker.api.saf.domain.AvsenderMottaker

object AvsenderMottakerTransformer {

    fun toInternal(external: HentJournalposter.AvsenderMottaker): AvsenderMottaker {
        val externalType: HentJournalposter.AvsenderMottakerIdType = external.type ?: throw MissingFieldException("type")
        val interalType = AvsenderMottakerTypeTransformer.toInternal(externalType)

        return AvsenderMottaker(
            external.id ?: throw MissingFieldException("id"),
            interalType
        )
    }

}
