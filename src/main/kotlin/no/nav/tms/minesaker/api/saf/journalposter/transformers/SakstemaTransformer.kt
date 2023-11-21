package no.nav.tms.minesaker.api.saf.journalposter.transformers

import no.nav.tms.minesaker.api.domain.JournalposterResponse

fun GraphQLSakstema.toInternal(innloggetBruker: String) = JournalposterResponse(
    temanavn = navn,
    temakode = kode.toInternalSaktemakode(),
    journalposter = journalposter.toInternal(innloggetBruker)
)
