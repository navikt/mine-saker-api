package no.nav.personbruker.minesaker.api.saf.sakstemaer.transformers

import no.nav.personbruker.minesaker.api.config.innsynsUrlResolverSingleton
import no.nav.personbruker.minesaker.api.domain.ForenkletSakstema
import no.nav.personbruker.minesaker.api.saf.journalposter.transformers.toInternalSaktemakode
import java.time.ZonedDateTime

fun GraphQLSakstema.toInternal(): ForenkletSakstema {
    val sakstemakode = kode.toInternalSaktemakode()
    return ForenkletSakstema(
        navn,
        sakstemakode,
        journalposter.toInternal(),
        innsynsUrlResolverSingleton.urlFor(sakstemakode)
    )
}

fun List<GraphQLJournalpost?>.toInternal(): ZonedDateTime? {
    val internalDates: List<ZonedDateTime> = filterNotNull().map { journalpost ->
        journalpost.relevanteDatoer.toInternal()
    }
    val sistEndret = internalDates.maxByOrNull { it }
    return sistEndret
}
