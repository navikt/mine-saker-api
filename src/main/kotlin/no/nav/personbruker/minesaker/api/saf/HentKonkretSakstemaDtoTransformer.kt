package no.nav.personbruker.minesaker.api.saf

import no.nav.dokument.saf.selvbetjening.generated.dto.HentKonkretSakstemaDTO
import no.nav.personbruker.minesaker.api.saf.dto.out.Sakstema

object HentKonkretSakstemaDtoTransformer {

    fun toInternal(externalTemaer: List<HentKonkretSakstemaDTO.Sakstema>): List<Sakstema> {
        return externalTemaer.map { external ->
            toInternal(external)
        }
    }

    fun toInternal(external: HentKonkretSakstemaDTO.Sakstema): Sakstema {
        return Sakstema(
            external.navn ?: "N/A",
            external.kode
        )
    }

    fun toInternal(externalData: HentKonkretSakstemaDTO.Result): List<Sakstema> {
        return externalData.dokumentoversiktSelvbetjening.tema.map { external ->
            toInternal(external)
        }
    }

}
