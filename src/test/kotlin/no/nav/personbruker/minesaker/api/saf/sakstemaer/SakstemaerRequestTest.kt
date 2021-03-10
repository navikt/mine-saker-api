package no.nav.personbruker.minesaker.api.saf.sakstemaer

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.personbruker.minesaker.api.saf.domain.Fodselsnummer
import org.amshove.kluent.`should contain`
import org.junit.jupiter.api.Test

internal class SakstemaerRequestTest {

    private val objectMapper = jacksonObjectMapper()

    private val dummyIdent = Fodselsnummer("123")

    @Test
    fun `Skal bygge opp en korrekt sporring`() {
        val expectedFields = "tema { navn kode }"
        val identAsQueryVarible = "\$ident : String!"
        val expectedAllSakstemar = """tema: []"""

        val request = SakstemaerRequest.create(dummyIdent)
        val requestAsJson = objectMapper.writeValueAsString(request)

        requestAsJson `should contain` expectedFields
        requestAsJson `should contain` identAsQueryVarible
        requestAsJson `should contain` expectedAllSakstemar
    }

}
