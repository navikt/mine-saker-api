package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.personbruker.minesaker.api.domain.JournalposterResponse

fun GraphQLSakstema.toInternal(innloggetBruker: String) = JournalposterResponse(
    temanavn = navn,
    temakode = kode.toInternalSaktemakode(),
    journalposter = journalposter.toInternal(innloggetBruker)
)
