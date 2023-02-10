package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.personbruker.minesaker.api.domain.Sakstema

fun GraphQLSakstema.toInternal(innloggetBruker: String) = Sakstema(
    navn,
    kode.toInternalSaktemakode(),
    journalposter.toInternal(innloggetBruker)
)
