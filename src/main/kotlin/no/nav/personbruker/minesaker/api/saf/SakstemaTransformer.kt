package no.nav.personbruker.minesaker.api.saf

import no.nav.personbruker.minesaker.api.saf.dto.out.Sakstema

object SakstemaTransformer {

    fun toInternal(externalTemaer: List<no.nav.personbruker.minesaker.api.saf.dto.`in`.Sakstema>): List<Sakstema> {
        val internalTeamer = mutableListOf<Sakstema>()
        externalTemaer.forEach { externalTema ->
            val internalSakstema = toInternal(externalTema)
            internalTeamer.add(internalSakstema)
        }
        return internalTeamer
    }

    fun toInternal(external: no.nav.personbruker.minesaker.api.saf.dto.`in`.Sakstema): Sakstema {
        return Sakstema(
            external.navn,
            external.kode
        )
    }

}
