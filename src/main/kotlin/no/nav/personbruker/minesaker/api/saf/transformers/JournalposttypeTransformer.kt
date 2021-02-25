package no.nav.personbruker.minesaker.api.saf.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.common.exception.MissingFieldException
import no.nav.personbruker.minesaker.api.common.exception.UnknownValueException
import no.nav.personbruker.minesaker.api.saf.domain.Journalposttype

object JournalposttypeTransformer {

    fun toInternal(external: HentJournalposter.Journalposttype?): Journalposttype {
        if (external == null) throw MissingFieldException("journalposttype")

        return when (external) {
            HentJournalposter.Journalposttype.I -> Journalposttype.INNGAAENDE
            HentJournalposter.Journalposttype.U -> Journalposttype.UTGAAENDE
            HentJournalposter.Journalposttype.N -> Journalposttype.NOTAT
            HentJournalposter.Journalposttype.__UNKNOWN_VALUE -> throw UnknownValueException("journalposttype")
        }
    }

}
