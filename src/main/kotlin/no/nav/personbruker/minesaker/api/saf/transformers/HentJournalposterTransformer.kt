package no.nav.personbruker.minesaker.api.saf.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.common.exception.MissingFieldException
import no.nav.personbruker.minesaker.api.saf.domain.Sakstema

object HentJournalposterTransformer {

    fun toInternal(externalTemaer: List<HentJournalposter.Sakstema>): List<Sakstema> {
        return externalTemaer.map { external ->
            toInternal(external)
        }
    }

    fun toInternal(external: HentJournalposter.Sakstema): Sakstema {
        return Sakstema(
            external.navn ?: throw MissingFieldException("navn"),
            external.kode ?: throw MissingFieldException("kode")
        )
    }

    fun toInternal(externalData: HentJournalposter.Result): List<Sakstema> {
        return externalData.dokumentoversiktSelvbetjening.tema.map { external ->
            toInternal(external)
        }
    }

}
