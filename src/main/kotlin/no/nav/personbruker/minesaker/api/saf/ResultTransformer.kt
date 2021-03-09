package no.nav.personbruker.minesaker.api.saf

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer
import no.nav.personbruker.minesaker.api.common.exception.SafException
import no.nav.personbruker.minesaker.api.saf.domain.Fodselsnummer
import no.nav.personbruker.minesaker.api.saf.domain.Sakstema
import no.nav.personbruker.minesaker.api.saf.journalposter.transformers.toInternal
import no.nav.personbruker.minesaker.api.saf.sakstemaer.toInternal

fun HentSakstemaer.Result.toInternal(): List<Sakstema> {
    val result = runCatching {
        dokumentoversiktSelvbetjening.tema.map { externalTema ->
            externalTema.toInternal()
        }

    }.onFailure { cause ->
        throw SafException("Klarte ikke å oversette svaret fra SAF til den interne domenemodellen.", cause)
    }
    return result.getOrThrow()
}

fun HentJournalposter.Result.toInternal(identInnloggetBruker: Fodselsnummer): List<Sakstema> {
    val result = runCatching {
        dokumentoversiktSelvbetjening.tema.map { externalTeama -> externalTeama.toInternal(identInnloggetBruker) }

    }.onFailure { cause ->
        throw SafException("Klarte ikke å oversette svaret fra SAF til den interne domenemodellen.", cause)
    }
    return result.getOrThrow()
}
