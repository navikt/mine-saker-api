package no.nav.personbruker.minesaker.api.saf.domain

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.amshove.kluent.`should contain`
import org.junit.jupiter.api.Test

internal class inlineClassesTest {

    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `Inline klasser skal ikke generere noe overhead naar det serialiseres til JSON`() {
        val dokumentinfo = Dokumentinfo(Tittel("Tittelen"), FilUUID("uuiden"), true)

        val dokumentInfoAsJson = objectMapper.writeValueAsString(dokumentinfo)

        println(dokumentInfoAsJson)

        dokumentInfoAsJson `should contain` """"filuuid":"uuiden""""
        dokumentInfoAsJson `should contain` """"tittel":"Tittelen""""
        dokumentInfoAsJson `should contain` """"brukerHarTilgang":true"""
    }

}
