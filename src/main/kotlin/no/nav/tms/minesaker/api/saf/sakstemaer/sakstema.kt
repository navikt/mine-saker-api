package no.nav.tms.minesaker.api.saf.sakstemaer

import no.nav.tms.minesaker.api.config.InnsynsUrlResolver
import java.time.ZonedDateTime

data class SistEndredeSakstemaer(
    val sistEndrede: List<ForenkletSakstema>,
    val dagpengerSistEndret: ZonedDateTime?
)

data class SakstemaerResponse(
    val sakerURL: String,
    val sakstemaer: List<ForenkletSakstema>,
    val dagpengerSistEndret: ZonedDateTime?)

data class ForenkletSakstema(
    val navn: String,
    val kode: Sakstemakode,
    val sistEndret: ZonedDateTime?,
    val detaljvisningUrl : String
)

fun SafSakstema.toInternal(innsynsUrlResolver: InnsynsUrlResolver): ForenkletSakstema {
    val sakstemakode = kode.toInternalSaktemakode()
    return ForenkletSakstema(
        navn,
        sakstemakode,
        journalposter.toInternal(),
        innsynsUrlResolver.urlFor(sakstemakode)
    )
}

fun List<SafJournalpost?>.toInternal(): ZonedDateTime? {
    val internalDates: List<ZonedDateTime> = filterNotNull().map { journalpost ->
        journalpost.relevanteDatoer.toInternal()
    }
    return internalDates.maxByOrNull { it }
}
