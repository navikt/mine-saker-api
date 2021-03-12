package no.nav.personbruker.minesaker.api.saf.journalposter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.personbruker.minesaker.api.saf.domain.Fodselsnummer
import no.nav.personbruker.minesaker.api.saf.domain.Sakstemakode
import org.amshove.kluent.`should contain`
import org.amshove.kluent.`should not contain`
import org.junit.jupiter.api.Test

internal class JournalposterRequestTest {

    private val objectMapper = jacksonObjectMapper()

    private val dummyIdent = Fodselsnummer("123")

    @Test
    fun `Skal bygge opp en korrekt sporring`() {
        val expectedSakstema = Sakstemakode.valueOf("FOR")
        val expectedFirstJournalpostFields = """journalposter{ tittel journalpostId journalposttype"""
        val identAsQueryVarible = "\$ident : String!"
        val sakstemaAsQueryVarible = "\$temaetSomSkalHentes : Tema"
        val expectedSakstemaAsInputVariable = """"temaetSomSkalHentes":"$expectedSakstema""""

        val request = JournalposterRequest.create(dummyIdent, expectedSakstema)

        val requestAsJson = objectMapper.writeValueAsString(request)

        requestAsJson `should contain` expectedFirstJournalpostFields
        requestAsJson `should contain` identAsQueryVarible
        requestAsJson `should contain` sakstemaAsQueryVarible
        requestAsJson `should contain` expectedSakstemaAsInputVariable
        requestAsJson `should contain` """"ident":"${dummyIdent.value}"""
    }

    @Test
    fun `Sporringen skal vare formatert til kompakt JSON`() {
        val request = JournalposterRequest.create(dummyIdent, Sakstemakode.valueOf("FOR"))

        val requestAsJson = objectMapper.writeValueAsString(request)

        requestAsJson `should not contain` "\\n"
        requestAsJson `should not contain` "\\r"
        requestAsJson `should not contain` "  "
    }

}
