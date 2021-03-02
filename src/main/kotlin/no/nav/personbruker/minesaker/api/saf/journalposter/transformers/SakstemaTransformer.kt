package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.common.exception.MissingFieldException
import no.nav.personbruker.minesaker.api.saf.domain.Sakstema

object SakstemaTransformer {

    fun toInternal(externalTemaer: List<HentJournalposter.Sakstema>): List<Sakstema> {
        return externalTemaer.map { external ->
            external.toInternal()
        }
    }

}

fun HentJournalposter.Sakstema.toInternal() = Sakstema(
    navn ?: throw MissingFieldException("navn"),
    kode ?: throw MissingFieldException("kode"),
    JournalpostTransformer.toInternal(journalposter)
)
