package no.nav.personbruker.minesaker.api.saf.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer
import no.nav.personbruker.minesaker.api.saf.domain.Sakstema

object ResultTransformer {

    fun toInternal(external: HentJournalposter.Result): List<Sakstema> {
        return SakstemaTransformer.toInternal(external.dokumentoversiktSelvbetjening.tema)
    }

    fun toInternal(external: HentSakstemaer.Result): List<Sakstema> {
        return HentSakstemaerTransformer.toInternal(external.dokumentoversiktSelvbetjening.tema)
    }

}
