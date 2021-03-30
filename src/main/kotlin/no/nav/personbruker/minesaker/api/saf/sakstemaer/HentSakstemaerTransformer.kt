package no.nav.personbruker.minesaker.api.saf.sakstemaer

import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer
import no.nav.personbruker.minesaker.api.common.exception.TransformationException
import no.nav.personbruker.minesaker.api.saf.domain.Navn
import no.nav.personbruker.minesaker.api.saf.domain.Sakstema
import no.nav.personbruker.minesaker.api.saf.journalposter.transformers.toInternalSaktemakode

fun HentSakstemaer.Sakstema.toInternal(): Sakstema {
    return Sakstema(
        Navn(navn ?: throw TransformationException.withMissingFieldName("navn")),
        kode?.toInternalSaktemakode() ?: throw TransformationException.withMissingFieldName("kode")
    )
}
