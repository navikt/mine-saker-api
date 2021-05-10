package no.nav.personbruker.minesaker.api.domain

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.personbruker.minesaker.api.config.enableMineSakerJsonConfig
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should contain`
import org.junit.jupiter.api.Test

internal class JournalpostTest {

    private val objectMapper = jacksonObjectMapper().enableMineSakerJsonConfig()

    @Test
    fun `Skal indikere at Journalposter uten dokumenter ikke har vedlegg`() {
        val journalpostUtenDokumenter = JournalpostObjectMother.giveMeJournalpostUtenDokumenter()

        journalpostUtenDokumenter.harVedlegg `should be equal to` false

        val asJson = objectMapper.writeValueAsString(journalpostUtenDokumenter)
        asJson `should contain` """"harVedlegg":false"""
    }

    @Test
    fun `Skal indikere at Journalposter med kun et dokument ikke har vedlegg`() {
        val journalpostUtenVedlegg = JournalpostObjectMother.giveMeJournalpostUtenVedlegg()

        journalpostUtenVedlegg.harVedlegg `should be equal to` false
        val asJson = objectMapper.writeValueAsString(journalpostUtenVedlegg)
        asJson `should contain` """"harVedlegg":false"""
    }

    @Test
    fun `Skal indikere at Journalposter med flere dokumenter har vedlegg`() {
        val journalpostMedVedlegg = JournalpostObjectMother.giveMeJournalpostMedVedlegg()

        journalpostMedVedlegg.harVedlegg `should be equal to` true
        val asJson = objectMapper.writeValueAsString(journalpostMedVedlegg)
        asJson `should contain` """"harVedlegg":true"""
    }

}
