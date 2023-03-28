package no.nav.personbruker.minesaker.api.saf

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer
import no.nav.personbruker.minesaker.api.config.InnsynsUrlResolver
import no.nav.personbruker.minesaker.api.domain.ForenkletSakstema
import no.nav.personbruker.minesaker.api.domain.Sakstema
import no.nav.personbruker.minesaker.api.saf.journalposter.transformers.toInternal
import no.nav.personbruker.minesaker.api.saf.sakstemaer.transformers.toInternal

fun HentSakstemaer.Result.toInternal(innsynsUrlResolver: InnsynsUrlResolver): List<ForenkletSakstema> =
    dokumentoversiktSelvbetjening.tema.map { externalTema -> externalTema.toInternal(innsynsUrlResolver) }

fun HentJournalposter.Result.toInternal(innloggetBruker: String): List<Sakstema> =
    dokumentoversiktSelvbetjening.tema.map { externalTema -> externalTema.toInternal(innloggetBruker) }
