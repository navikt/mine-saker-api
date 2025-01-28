package no.nav.tms.minesaker.api.journalpost

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import no.nav.tms.minesaker.api.journalpost.query.AlleJournalposterRequest
import org.junit.jupiter.api.Test

internal class JournalposterRequestV2Test {

    private val objectMapper = jacksonObjectMapper()

    private val dummyIdent = "123"

    @Test
    fun `Skal bygge opp en korrekt sporring`() {
        val expectedFirstJournalpostFields = """journalposter { tittel journalpostId journalposttype"""
        val identAsQueryVarible = "\$ident : String!"

        val request = AlleJournalposterRequest.create(dummyIdent)

        val requestAsJson = objectMapper.writeValueAsString(request)

        requestAsJson shouldContain expectedFirstJournalpostFields
        requestAsJson shouldContain identAsQueryVarible
        requestAsJson shouldContain """"ident":"$dummyIdent"""
    }

    @Test
    fun `Sporringen skal vare formatert til kompakt JSON`() {
        val request = AlleJournalposterRequest.create(dummyIdent)

        val requestAsJson = objectMapper.writeValueAsString(request)

        requestAsJson shouldNotContain "\\n"
        requestAsJson shouldNotContain "\\r"
        requestAsJson shouldNotContain "  "
    }

}
