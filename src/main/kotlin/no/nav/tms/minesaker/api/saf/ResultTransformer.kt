package no.nav.tms.minesaker.api.saf

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer
import no.nav.tms.minesaker.api.config.InnsynsUrlResolver
import no.nav.tms.minesaker.api.domain.JournalposterResponse
import no.nav.tms.minesaker.api.saf.journalposter.transformers.toInternal
import no.nav.tms.minesaker.api.saf.sakstemaer.transformers.toInternal
import no.nav.tms.minesaker.api.sak.SakstemaResult

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
