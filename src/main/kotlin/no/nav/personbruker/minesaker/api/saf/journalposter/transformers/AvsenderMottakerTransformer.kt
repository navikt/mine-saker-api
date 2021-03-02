package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.common.exception.MissingFieldException
import no.nav.personbruker.minesaker.api.saf.domain.AvsenderMottaker

object AvsenderMottakerTransformer {

    fun toInternal(external: HentJournalposter.AvsenderMottaker?): AvsenderMottaker {
        if (external == null) throw MissingFieldException("avsenderMottaker")

        return external.toInternal()
    }

}

fun HentJournalposter.AvsenderMottaker.toInternal() = AvsenderMottaker(
    id,
    AvsenderMottakerTypeTransformer.toInternal(type)
)
