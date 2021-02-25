package no.nav.personbruker.minesaker.api.saf.domain

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.amshove.kluent.`should contain`
import org.amshove.kluent.`should not contain`
import org.junit.jupiter.api.Test

internal class AvsenderMottakerTest {

    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `Det skal ikke vaere mulig aa skrive ut id-en vha toString-metoden`() {
        val avsender = AvsenderMottakerObjectMother.giveMeAvsenderPerson()

        val toStringContents = avsender.toString()

        toStringContents `should not contain` avsender.id!!
        toStringContents `should contain` "id='***'"
        toStringContents `should contain` avsender.type.toString()
    }

    @Test
    fun `ID-en til avsender eller mottaker skal ikke sendes videre til frontenden`() {
        val avsender = AvsenderMottakerObjectMother.giveMeAvsenderPerson()

        val avsenderAsJson = objectMapper.writeValueAsString(avsender)

        avsenderAsJson `should not contain` avsender.id!!
        avsenderAsJson `should not contain` """"id""""
        avsenderAsJson `should contain` avsender.type.toString()
    }

}
