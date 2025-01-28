package no.nav.tms.minesaker.api.journalpost

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tms.minesaker.api.journalpost.query.AlleJournalposterRequest
import no.nav.tms.minesaker.api.journalpost.query.HentJournalpostV2Request
import no.nav.tms.minesaker.api.setup.TokendingsExchange
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser

class SafService(
    private val safConsumer: SafConsumer,
    private val tokendingsExchange: TokendingsExchange
) {

    private val log = KotlinLogging.logger { }

    suspend fun hentDokumentStream(
        user: IdportenUser,
        journapostId: String,
        dokumentinfoId: String,
        receiver: suspend (DokumentStream) -> Unit
    ) {
        log.info { "Henter dokument $dokumentinfoId fra journalposten $journapostId" }
        val exchangedToken = tokendingsExchange.safToken(user)
        safConsumer.hentDokument(journapostId, dokumentinfoId, exchangedToken, receiver)
    }

    suspend fun alleJournalposter(user: IdportenUser, representert: String?): List<Journalpost> =
        if (representert != null) {
            log.info { "Henter alle journalposter for representert fra SAF" }

            safConsumer.alleJournalposter(
                request = AlleJournalposterRequest.create(representert),
                accessToken = tokendingsExchange.safToken(user)
            )
        } else {
            log.info { "Henter alle journalposter for bruker fra SAF" }

            safConsumer.alleJournalposter(
                request = AlleJournalposterRequest.create(user.ident),
                accessToken = tokendingsExchange.safToken(user)
            )
        }

    suspend fun hentJournalpost(user: IdportenUser, journapostId: String, representert: String?): Journalpost? {
        if (representert != null) {
            log.info { "Henter enkelt journalpost for representert fra SAF" }
        } else {
            log.info { "Henter enkelt journalpost for bruker fra SAF" }
        }

        return safConsumer.hentJournalpost(
            request = HentJournalpostV2Request.create(journapostId),
            accessToken = tokendingsExchange.safToken(user)
        )
    }

    suspend fun sisteJournalposter(user: IdportenUser, antall: Int): List<ForenkletJournalpost> {

        val journalposter = safConsumer.alleJournalposter(
            request = AlleJournalposterRequest.create(user.ident),
            accessToken = tokendingsExchange.safToken(user)
        ).sortedByDescending { it.opprettet }
            .map {
                ForenkletJournalpost(
                    journalpostId = it.journalpostId,
                    tittel = it.tittel,
                    temakode = it.temakode,
                    avsender = it.avsender,
                    mottaker = it.mottaker,
                    opprettet = it.opprettet,
                    dokumentInfoId = it.dokument.dokumentInfoId,
                )
            }

        return if (journalposter.isEmpty()) {
            emptyList()
        } else {
            journalposter.take(antall)
        }
    }
}
