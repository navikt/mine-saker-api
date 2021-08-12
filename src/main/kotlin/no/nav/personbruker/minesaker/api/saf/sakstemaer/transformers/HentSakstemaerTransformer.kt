package no.nav.personbruker.minesaker.api.saf.sakstemaer

import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer
import no.nav.personbruker.minesaker.api.domain.ForenkletSakstema
import no.nav.personbruker.minesaker.api.domain.Navn
import no.nav.personbruker.minesaker.api.saf.journalposter.transformers.toInternalSaktemakode
import no.nav.personbruker.minesaker.api.saf.sakstemaer.transformers.toInternal
import java.time.ZonedDateTime

fun HentSakstemaer.Sakstema.toInternal(): ForenkletSakstema {
    return ForenkletSakstema(
        Navn(navn),
        kode.toInternalSaktemakode(),
        journalposter.toInternal()
    )
}

fun List<HentSakstemaer.Journalpost?>.toInternal(): ZonedDateTime? {
    val internalDates: List<ZonedDateTime> = filterNotNull().map { journalpost -> journalpost.relevanteDatoer.toInternal() }
    val sistEndret = internalDates.maxByOrNull { it }
    return sistEndret
}
