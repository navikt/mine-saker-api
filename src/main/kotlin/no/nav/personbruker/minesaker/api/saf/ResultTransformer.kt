package no.nav.personbruker.minesaker.api.saf

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer
import no.nav.personbruker.minesaker.api.config.InnsynsUrlResolver
import no.nav.personbruker.minesaker.api.domain.JournalposterResponse
import no.nav.personbruker.minesaker.api.saf.journalposter.transformers.toInternal
import no.nav.personbruker.minesaker.api.saf.sakstemaer.transformers.toInternal
import no.nav.personbruker.minesaker.api.sak.SakstemaResult

fun HentSakstemaer.Result.toInternal(innsynsUrlResolver: InnsynsUrlResolver): SakstemaResult =
    SakstemaResult(dokumentoversiktSelvbetjening.tema
        .map { externalTema -> externalTema.toInternal(innsynsUrlResolver) }
    )

fun HentJournalposter.Result.toInternal(innloggetBruker: String): JournalposterResponse? =
    dokumentoversiktSelvbetjening
        .tema
        .map {
            externalTema -> externalTema.toInternal(innloggetBruker)
        }.firstOrNull()
