package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.saf.domain.RelevantDato

object RelevantDatoTransformer {

    fun toInternal(relevanteDatoer: List<HentJournalposter.RelevantDato?>): List<RelevantDato> {
        return relevanteDatoer
            .filterNotNull()
            .map { external ->
                external.toInternal()
            }
    }

}

fun HentJournalposter.RelevantDato.toInternal() = RelevantDato(
    dato.toInternal(),
    datotype.toInternal()
)
