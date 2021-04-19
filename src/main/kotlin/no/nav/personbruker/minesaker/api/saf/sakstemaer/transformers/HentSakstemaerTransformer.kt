package no.nav.personbruker.minesaker.api.saf.sakstemaer

import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer
import no.nav.personbruker.minesaker.api.common.exception.TransformationException
import no.nav.personbruker.minesaker.api.saf.common.transformers.finnSistEndret
import no.nav.personbruker.minesaker.api.saf.domain.ForenkletSakstema
import no.nav.personbruker.minesaker.api.saf.domain.Navn
import no.nav.personbruker.minesaker.api.saf.journalposter.transformers.toInternalSaktemakode
import no.nav.personbruker.minesaker.api.saf.sakstemaer.transformers.toInternal
import java.time.ZonedDateTime

fun HentSakstemaer.Sakstema.toInternal(): ForenkletSakstema {
    return ForenkletSakstema(
        Navn(navn ?: throw TransformationException.withMissingFieldName("navn")),
        kode?.toInternalSaktemakode() ?: throw TransformationException.withMissingFieldName("kode"),
        journalposter.toInternal()
    )
}

fun List<HentSakstemaer.Journalpost?>.toInternal(): ZonedDateTime? {
    val internalDates: List<ZonedDateTime> = filterNotNull().map { journalpost -> journalpost.relevanteDatoer.toInternal() }
    val sistEndret = internalDates.maxByOrNull { it }
    return sistEndret
}
