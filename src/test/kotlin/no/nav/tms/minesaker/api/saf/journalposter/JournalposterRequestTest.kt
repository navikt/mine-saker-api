package no.nav.tms.minesaker.api.saf.journalposter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain

import no.nav.tms.minesaker.api.saf.sakstemaer.Sakstemakode
import no.nav.tms.minesaker.api.saf.journalposter.v1.JournalposterRequest
import org.junit.jupiter.api.Test

internal class JournalposterRequestTest {

    private val objectMapper = jacksonObjectMapper()

    private val dummyIdent = "123"

    @Test
    fun `Skal bygge opp en korrekt sporring`() {
        val expectedSakstema = Sakstemakode.valueOf("FOR")
        val expectedFirstJournalpostFields = """journalposter{ tittel journalpostId journalposttype"""
        val identAsQueryVarible = "\$ident : String!"
        val sakstemaAsQueryVarible = "\$temaetSomSkalHentes : Tema"
        val expectedSakstemaAsInputVariable = """"temaetSomSkalHentes":"$expectedSakstema""""

        val request = JournalposterRequest.create(dummyIdent, expectedSakstema)

        val requestAsJson = objectMapper.writeValueAsString(request)

        requestAsJson shouldContain expectedFirstJournalpostFields
        requestAsJson shouldContain identAsQueryVarible
        requestAsJson shouldContain sakstemaAsQueryVarible
        requestAsJson shouldContain expectedSakstemaAsInputVariable
        requestAsJson shouldContain """"ident":"$dummyIdent"""
    }

    @Test
    fun `Sporringen skal vare formatert til kompakt JSON`() {
        val request = JournalposterRequest.create(dummyIdent, Sakstemakode.valueOf("FOR"))

        val requestAsJson = objectMapper.writeValueAsString(request)

        requestAsJson shouldNotContain "\\n"
        requestAsJson shouldNotContain "\\r"
        requestAsJson shouldNotContain "  "
    }

}
