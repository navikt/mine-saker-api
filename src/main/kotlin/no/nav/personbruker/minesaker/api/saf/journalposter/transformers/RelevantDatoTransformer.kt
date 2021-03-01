package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.saf.domain.RelevantDato

object RelevantDatoTransformer {

    fun toInternal(relevanteDatoer: List<HentJournalposter.RelevantDato?>): List<RelevantDato> {
        return relevanteDatoer
            .filterNotNull()
            .map { external ->
                toInternal(external)
            }
    }

    internal fun toInternal(external: HentJournalposter.RelevantDato): RelevantDato {
        return RelevantDato(
            DateTimeTransformer.toInternal(external.dato),
            DatotypeTransformer.toInternal(external.datotype)
        )
    }

}
