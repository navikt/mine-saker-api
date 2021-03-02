package no.nav.personbruker.minesaker.api.saf

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer
import no.nav.personbruker.minesaker.api.saf.domain.Sakstema
import no.nav.personbruker.minesaker.api.saf.journalposter.transformers.toInternal
import no.nav.personbruker.minesaker.api.saf.sakstemaer.toInternal

fun HentSakstemaer.Result.toInternal(): List<Sakstema> {
    return dokumentoversiktSelvbetjening.tema.map { externalTema -> externalTema.toInternal() }
}

fun HentJournalposter.Result.toInternal(): List<Sakstema> {
    return dokumentoversiktSelvbetjening.tema.map { externalTeama -> externalTeama.toInternal() }
}
