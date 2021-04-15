package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.saf.domain.Dato

fun HentJournalposter.RelevantDato.toInternal() = Dato(
    dato.toInternal(),
    datotype.toInternal()
)
