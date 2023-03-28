package no.nav.personbruker.minesaker.api.saf.sakstemaer.transformers

import no.nav.personbruker.minesaker.api.config.InnsynsUrlResolver
import no.nav.personbruker.minesaker.api.domain.ForenkletSakstema
import no.nav.personbruker.minesaker.api.saf.journalposter.transformers.toInternalSaktemakode
import java.time.ZonedDateTime

fun GraphQLSakstema.toInternal(innsynsUrlResolver: InnsynsUrlResolver): ForenkletSakstema {
    val sakstemakode = kode.toInternalSaktemakode()
    return ForenkletSakstema(
        navn,
        sakstemakode,
        journalposter.toInternal(),
        innsynsUrlResolver.urlFor(sakstemakode)
    )
}

fun List<GraphQLJournalpost?>.toInternal(): ZonedDateTime? {
    val internalDates: List<ZonedDateTime> = filterNotNull().map { journalpost ->
        journalpost.relevanteDatoer.toInternal()
    }
    return internalDates.maxByOrNull { it }
}
