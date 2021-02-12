package no.nav.personbruker.minesaker.api.saf

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.personbruker.minesaker.api.saf.queries.HentKonkretSakstema
import org.amshove.kluent.`should contain`
import org.amshove.kluent.`should not contain`
import org.junit.jupiter.api.Test

internal class HentKonkretSakstemaTest {

    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `Skal bygge opp en korrekt sporring`() {
        val expectedSakstema = "FOR"
        val expectedFields = "tema { navn kode }"
        val sakstemaAsQueryVarible = "query(\$temaetSomSkalHentes : Tema)"
        val expectedSakstemaAsInputVariable = """"temaetSomSkalHentes":"$expectedSakstema""""

        val request = HentKonkretSakstema.createRequest(expectedSakstema)

        val requestAsJson = objectMapper.writeValueAsString(request)

        println(requestAsJson)

        requestAsJson `should contain` expectedFields
        requestAsJson `should contain` sakstemaAsQueryVarible
        requestAsJson `should contain` expectedSakstemaAsInputVariable
    }

    @Test
    fun `Sporringen skal vare formatert til kompakt JSON`() {
        val request = HentKonkretSakstema.createRequest("FOR")

        val requestAsJson = objectMapper.writeValueAsString(request)

        requestAsJson `should not contain` "\\n"
        requestAsJson `should not contain` "\\r"
        requestAsJson `should not contain` "  "
    }

}
