package no.nav.personbruker.minesaker.api.saf

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.saf.dto.out.Sakstema

object HentJournalposterTransformer {

    fun toInternal(externalTemaer: List<HentJournalposter.Sakstema>): List<Sakstema> {
        return externalTemaer.map { external ->
            toInternal(external)
        }
    }

    fun toInternal(external: HentJournalposter.Sakstema): Sakstema {
        return Sakstema(
            external.navn ?: "N/A",
            external.kode
        )
    }

    fun toInternal(externalData: HentJournalposter.Result): List<Sakstema> {
        return externalData.dokumentoversiktSelvbetjening.tema.map { external ->
            toInternal(external)
        }
    }

}
