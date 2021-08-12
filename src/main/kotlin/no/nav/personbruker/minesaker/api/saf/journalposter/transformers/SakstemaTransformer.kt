package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.domain.Fodselsnummer
import no.nav.personbruker.minesaker.api.domain.Navn
import no.nav.personbruker.minesaker.api.domain.Sakstema

fun HentJournalposter.Sakstema.toInternal(innloggetBruker: Fodselsnummer) = Sakstema(
    Navn(navn),
    kode.toInternalSaktemakode(),
    journalposter.toInternal(innloggetBruker)
)
