package no.nav.personbruker.minesaker.api.saf.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.DateTime
import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.common.exception.MissingFieldException
import no.nav.personbruker.minesaker.api.saf.domain.Journalposttype
import java.time.ZonedDateTime

object RelevantDatoTransformer {

    private val timeZoneUTC = "+00:00"

    fun toInternal(
        relevanteDatoer: List<HentJournalposter.RelevantDato?>,
        journalposttype: Journalposttype
    ): ZonedDateTime {
        val datoerUtenNullverdier = relevanteDatoer.filterNotNull()
        return when (journalposttype) {
            Journalposttype.UTGAAENDE -> toInternalUtgaaende(datoerUtenNullverdier)
            Journalposttype.INNGAAENDE -> toInternalInngaaende(datoerUtenNullverdier)
            Journalposttype.NOTAT -> toInternalNotat(datoerUtenNullverdier)
        }

    }

    private fun toInternalUtgaaende(relevanteDatoer: List<HentJournalposter.RelevantDato>): ZonedDateTime {
        val onsketDato = hentOnsketDato(relevanteDatoer, HentJournalposter.Datotype.DATO_EKSPEDERT)
        return toInternal(onsketDato)
    }

    private fun toInternalInngaaende(relevanteDatoer: List<HentJournalposter.RelevantDato>): ZonedDateTime {
        val onsketDato = hentOnsketDato(relevanteDatoer, HentJournalposter.Datotype.DATO_REGISTRERT)
        return toInternal(onsketDato)
    }

    private fun toInternalNotat(relevanteDatoer: List<HentJournalposter.RelevantDato>): ZonedDateTime {
        val onsketDato = hentOnsketDato(relevanteDatoer, HentJournalposter.Datotype.DATO_OPPRETTET)
        return toInternal(onsketDato)
    }

    private fun hentOnsketDato(
        relevanteDatoer: List<HentJournalposter.RelevantDato>,
        onsketDato: HentJournalposter.Datotype
    ): DateTime {
        relevanteDatoer
            .forEach { relevantDato ->
                if (relevantDato.datotype === onsketDato) {
                    return relevantDato.dato
                }
            }
        throw MissingFieldException(onsketDato.toString().toLowerCase())
    }

    internal fun toInternal(dato: DateTime): ZonedDateTime {
        return ZonedDateTime.parse("$dato$timeZoneUTC")
    }

}
