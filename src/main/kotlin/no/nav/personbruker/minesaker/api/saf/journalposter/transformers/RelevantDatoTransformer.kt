package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.saf.domain.RelevantDato

fun HentJournalposter.RelevantDato.toInternal() = RelevantDato(
    dato.toInternal(),
    datotype.toInternal()
)
