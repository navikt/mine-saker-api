package no.nav.tms.minesaker.api

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tms.minesaker.api.digisos.DigiSosConsumer
import no.nav.tms.minesaker.api.saf.SafConsumer
import no.nav.tms.minesaker.api.setup.TokendingsExchange
import no.nav.tms.minesaker.api.saf.DokumentStream
import no.nav.tms.minesaker.api.saf.journalposter.*
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser

class SakService(
    private val safConsumer: SafConsumer,
    private val tokendingsExchange: TokendingsExchange,
    private val digiSosConsumer: DigiSosConsumer,
) {

    private val log = KotlinLogging.logger { }
    private val secureLog = KotlinLogging.logger("secureLog")

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

    suspend fun alleJournalposter(user: IdportenUser, representert: String?): List<JournalpostV2> =
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

    suspend fun harInnsendteHosDigiSos(user: IdportenUser): Boolean = try {
        val exchangedToken = tokendingsExchange.digisosToken(user)
        digiSosConsumer.harInnsendte(exchangedToken)
    } catch (e: Exception) {
        log.error { "Klarte ikke å hente brukers data fra DigiSos." }
        secureLog.error(e) { "Klarte ikke å hente brukers (${user.ident}) data fra DigiSos." }
        false
    }



    suspend fun hentJournalpost(user: IdportenUser, journapostId: String, representert: String?): JournalpostV2? {
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

    suspend fun sisteJournalposter(user: IdportenUser, antall: Int): List<ForenkletJournalpostV2> {

        val journalposter = safConsumer.alleJournalposter(
            request = AlleJournalposterRequest.create(user.ident),
            accessToken = tokendingsExchange.safToken(user)
        ).sortedByDescending { it.opprettet }
            .map {
                ForenkletJournalpostV2(
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
