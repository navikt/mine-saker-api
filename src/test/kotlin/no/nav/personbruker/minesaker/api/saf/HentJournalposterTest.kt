package no.nav.personbruker.minesaker.api.saf

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.personbruker.minesaker.api.saf.queries.HentJournalposter
import org.amshove.kluent.`should contain`
import org.amshove.kluent.`should not contain`
import org.junit.jupiter.api.Test

internal class HentJournalposterTest {

    private val objectMapper = jacksonObjectMapper()

    private val dummyIdent = "123"

    @Test
    fun `Skal bygge opp en korrekt sporring`() {
        val expectedSakstema = "FOR"
        val expectedFields = "tema { navn kode }"
        val identAsQueryVarible = "\$ident : String!"
        val sakstemaAsQueryVarible = "\$temaetSomSkalHentes : Tema"
        val expectedSakstemaAsInputVariable = """"temaetSomSkalHentes":"$expectedSakstema""""

        val request = HentJournalposter.createRequest(dummyIdent, expectedSakstema)

        val requestAsJson = objectMapper.writeValueAsString(request)

        requestAsJson `should contain` expectedFields
        requestAsJson `should contain` identAsQueryVarible
        requestAsJson `should contain` sakstemaAsQueryVarible
        requestAsJson `should contain` expectedSakstemaAsInputVariable
    }

    @Test
    fun `Sporringen skal vare formatert til kompakt JSON`() {
        val request = HentJournalposter.createRequest(dummyIdent, "FOR")

        val requestAsJson = objectMapper.writeValueAsString(request)

        requestAsJson `should not contain` "\\n"
        requestAsJson `should not contain` "\\r"
        requestAsJson `should not contain` "  "
    }

}
