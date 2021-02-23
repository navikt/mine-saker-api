package no.nav.personbruker.minesaker.api.saf

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.common.exception.MissingFieldException
import no.nav.personbruker.minesaker.api.saf.domain.MinimaltSakstema

object HentJournalposterTransformer {

    fun toInternal(externalTemaer: List<HentJournalposter.Sakstema>): List<MinimaltSakstema> {
        return externalTemaer.map { external ->
            toInternal(external)
        }
    }

    fun toInternal(external: HentJournalposter.Sakstema): MinimaltSakstema {
        return MinimaltSakstema(
            external.navn ?: throw MissingFieldException("navn"),
            external.kode ?: throw MissingFieldException("kode")
        )
    }

    fun toInternal(externalData: HentJournalposter.Result): List<MinimaltSakstema> {
        return externalData.dokumentoversiktSelvbetjening.tema.map { external ->
            toInternal(external)
        }
    }

}
