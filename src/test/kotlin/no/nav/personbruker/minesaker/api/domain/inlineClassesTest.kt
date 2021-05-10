package no.nav.personbruker.minesaker.api.domain

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.personbruker.minesaker.api.domain.*
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should contain`
import org.junit.jupiter.api.Test

internal class inlineClassesTest {

    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `Inline klasser skal ikke generere noe overhead naar det serialiseres til JSON`() {
        val dokumentinfo = Dokumentinfo(Tittel("Tittelen"), DokumentInfoId("filid"), Dokumenttype.HOVED,true, emptyList())

        val dokumentInfoAsJson = objectMapper.writeValueAsString(dokumentinfo)

        dokumentInfoAsJson `should contain` """"dokumentInfoId":"filid""""
        dokumentInfoAsJson `should contain` """"tittel":"Tittelen""""
        dokumentInfoAsJson `should contain` """"brukerHarTilgang":true"""
        dokumentInfoAsJson `should contain` """"eventuelleGrunnerTilManglendeTilgang":[]"""
        dokumentInfoAsJson `should contain` """"dokumenttype":"HOVED"""
    }

    @Test
    fun `DokumentInfoId skal skrive ut sin egen value ved kall til toString-metoden`() {
        val expectedValue = "expectedValue"
        val id = DokumentInfoId(expectedValue)

        id.toString() `should be equal to` expectedValue
    }

    @Test
    fun `JournalpostId skal skrive ut sin egen value ved kall til toString-metoden`() {
        val expectedValue = "expectedValue"
        val id = JournalpostId(expectedValue)

        id.toString() `should be equal to` expectedValue
    }

}
