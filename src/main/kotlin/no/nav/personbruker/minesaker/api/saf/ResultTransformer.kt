package no.nav.personbruker.minesaker.api.saf

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer
import no.nav.personbruker.minesaker.api.domain.Fodselsnummer
import no.nav.personbruker.minesaker.api.domain.ForenkletSakstema
import no.nav.personbruker.minesaker.api.domain.Sakstema
import no.nav.personbruker.minesaker.api.saf.journalposter.transformers.toInternal
import no.nav.personbruker.minesaker.api.saf.sakstemaer.transformers.toInternal

fun HentSakstemaer.Result.toInternal(): List<ForenkletSakstema> =
    dokumentoversiktSelvbetjening.tema.map { externalTema -> externalTema.toInternal() }

fun HentJournalposter.Result.toInternal(innloggetBruker: Fodselsnummer): List<Sakstema> =
    dokumentoversiktSelvbetjening.tema.map { externalTema -> externalTema.toInternal(innloggetBruker) }
