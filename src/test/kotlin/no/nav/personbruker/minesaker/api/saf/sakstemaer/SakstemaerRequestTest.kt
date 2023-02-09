package no.nav.personbruker.minesaker.api.saf.sakstemaer

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.matchers.string.shouldContain

import org.junit.jupiter.api.Test

internal class SakstemaerRequestTest {

    private val objectMapper = jacksonObjectMapper()

    private val dummyIdent = "123"

    @Test
    fun `Skal bygge opp en korrekt sporring`() {
        val expectedFields = "tema { navn kode journalposter{ relevanteDatoer { dato } } }"
        val identAsQueryVarible = "\$ident : String!"
        val expectedAllSakstemar = """tema: []"""

        val request = SakstemaerRequest.create(dummyIdent)
        val requestAsJson = objectMapper.writeValueAsString(request)

        requestAsJson shouldContain expectedFields
        requestAsJson shouldContain identAsQueryVarible
        requestAsJson shouldContain expectedAllSakstemar
        requestAsJson shouldContain """"ident":"$dummyIdent"""
    }

}
