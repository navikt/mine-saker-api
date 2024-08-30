package no.nav.tms.minesaker.api.saf.journalposterv2

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain

import no.nav.tms.minesaker.api.saf.sakstemaer.Sakstemakode
import no.nav.tms.minesaker.api.saf.journalposter.v2.HentJournalposterV2Request
import org.junit.jupiter.api.Test

internal class JournalposterRequestV2Test {

    private val objectMapper = jacksonObjectMapper()

    private val dummyIdent = "123"

    @Test
    fun `Skal bygge opp en korrekt sporring`() {
        val expectedSakstema = Sakstemakode.valueOf("FOR")
        val expectedFirstJournalpostFields = """journalposter { tittel journalpostId journalposttype"""
        val identAsQueryVarible = "\$ident : String!"
        val sakstemaAsQueryVarible = "\$sakstema : Tema"
        val expectedSakstemaAsInputVariable = """"sakstema":"$expectedSakstema""""

        val request = HentJournalposterV2Request.create(dummyIdent, expectedSakstema)

        val requestAsJson = objectMapper.writeValueAsString(request)

        requestAsJson shouldContain expectedFirstJournalpostFields
        requestAsJson shouldContain identAsQueryVarible
        requestAsJson shouldContain sakstemaAsQueryVarible
        requestAsJson shouldContain expectedSakstemaAsInputVariable
        requestAsJson shouldContain """"ident":"$dummyIdent"""
    }

    @Test
    fun `Sporringen skal vare formatert til kompakt JSON`() {
        val request = HentJournalposterV2Request.create(dummyIdent, Sakstemakode.valueOf("FOR"))

        val requestAsJson = objectMapper.writeValueAsString(request)

        requestAsJson shouldNotContain "\\n"
        requestAsJson shouldNotContain "\\r"
        requestAsJson shouldNotContain "  "
    }

}
