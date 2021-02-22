package no.nav.personbruker.minesaker.api.saf

import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakerDTO
import no.nav.personbruker.minesaker.api.saf.dto.out.Sakstema

object HentSakerDtoTransformer {

    fun toInternal(externalTemaer: List<HentSakerDTO.Sakstema>): List<Sakstema> {
        return externalTemaer.map { external ->
            toInternal(external)
        }
    }

    fun toInternal(external: HentSakerDTO.Sakstema): Sakstema {
        return Sakstema(
            external.navn ?: "N/A",
            external.kode
        )
    }

    fun toInternal(externalData: HentSakerDTO.Result): List<Sakstema> {
        return externalData.dokumentoversiktSelvbetjening.tema.map { external ->
            toInternal(external)
        }
    }

}
