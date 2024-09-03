package no.nav.tms.minesaker.api.saf.journalposter.v1

import no.nav.tms.minesaker.api.saf.sakstemaer.SakstemaException
import no.nav.tms.minesaker.api.saf.sakstemaer.Sakstemakode
import no.nav.tms.minesaker.api.saf.sakstemaer.toInternalSaktemakode
import java.time.ZonedDateTime

data class JournalposterResponse(
    val temanavn: String,
    val temakode: Sakstemakode,
    val journalposter: List<Journalpost> = emptyList()
) {
    val navn get() = temanavn
    val kode get() = temakode
}

data class Journalpost(
    val tittel: String,
    val journalpostId: String,
    val journalposttype: Journalposttype,
    val avsender: Dokumentkilde?,
    val mottaker: Dokumentkilde?,
    val sisteEndret: ZonedDateTime,
    val dokumenter: List<Dokumentinfo> = emptyList(),
    val harVedlegg: Boolean = dokumenter.size > 1
)

enum class Journalposttype {
    INNGAAENDE,
    UTGAAENDE,
    NOTAT
}

fun SafSakstema.toInternal(innloggetBruker: String) = JournalposterResponse(
    temanavn = navn,
    temakode = kode.toInternalSaktemakode(),
    journalposter = journalposter.toInternal(innloggetBruker)
)

fun List<SafJournalpost?>.toInternal(innloggetBruker: String): List<Journalpost> {
    return filterNotNull().map { external -> external.toInternal(innloggetBruker) }
}

fun SafJournalpost.toInternal(innloggetBruker: String) = Journalpost(
    tittel ?: "Uten tittel",
    journalpostId,
    journalposttype.toInternal(),
    avsender?.toInternal(innloggetBruker),
    mottaker?.toInternal(innloggetBruker),
    relevanteDatoer.toInternal(),
    dokumenter?.toInternal() ?: throw SakstemaException.withMissingFieldName("dokumenter")
)

fun SafJournalposttype.toInternal(): Journalposttype {
    return when (this) {
        SafJournalposttype.I -> Journalposttype.INNGAAENDE
        SafJournalposttype.U -> Journalposttype.UTGAAENDE
        SafJournalposttype.N -> Journalposttype.NOTAT
        SafJournalposttype.__UNKNOWN_VALUE -> throw buildException()
    }
}

private fun buildException(): SakstemaException {
    val message = "Mottok ukjent verdi for feltet 'journalposttype'."
    return SakstemaException(message, SakstemaException.ErrorType.UNKNOWN_VALUE)
}
