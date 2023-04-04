package no.nav.personbruker.minesaker.api.domain

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test

internal class JournalpostTest {

    private val objectMapper = jacksonObjectMapper().apply {
        registerKotlinModule()
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @Test
    fun `Skal indikere at Journalposter uten dokumenter ikke har vedlegg`() {
        val journalpostUtenDokumenter = DomainTestData.journalpostUtenDokumenter()

        journalpostUtenDokumenter.harVedlegg shouldBe false

        val asJson = objectMapper.writeValueAsString(journalpostUtenDokumenter)
        asJson shouldContain """"harVedlegg":false"""
    }

    @Test
    fun `Skal indikere at Journalposter med kun et dokument ikke har vedlegg`() {
        val journalpostUtenVedlegg = DomainTestData.journalpostUtenDokumenter(
            tittel = "Uten vedlegg",
            dokumenter = listOf(DomainTestData.hoveddokument())
        )

        journalpostUtenVedlegg.harVedlegg shouldBe false
        val asJson = objectMapper.writeValueAsString(journalpostUtenVedlegg)
        asJson shouldContain """"harVedlegg":false"""
    }

    @Test
    fun `Skal indikere at Journalposter med flere dokumenter har vedlegg`() {
        val journalpostMedVedlegg = DomainTestData.journalpostUtenDokumenter(
            tittel = "Med vedlegg",
            dokumenter = listOf(
                DomainTestData.hoveddokument(),
                DomainTestData.hoveddokument(
                    tittel = "Vedlegg1",
                    type = Dokumenttype.VEDLEGG
                )
            )
        )

        journalpostMedVedlegg.harVedlegg shouldBe true
        val asJson = objectMapper.writeValueAsString(journalpostMedVedlegg)
        asJson shouldContain """"harVedlegg":true"""
    }

}
