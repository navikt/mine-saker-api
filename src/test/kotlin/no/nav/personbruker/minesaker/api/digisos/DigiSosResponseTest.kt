package no.nav.personbruker.minesaker.api.digisos

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.personbruker.minesaker.api.config.enableMineSakerJsonConfig
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test

internal class DigiSosResponseTest {

    private val responsFraDigiSos = """
        [
            {
                "navn": "Økonomisk sosialhjelp",
                "kode": "KOM",
                "sistEndret": "2021-06-14T07:03:46.328845"
            }
        ]
    """.trimIndent()

    private val objectMapper = jacksonObjectMapper().enableMineSakerJsonConfig()

    @Test
    fun `Skal kunne konvertere til intern type`() {
        val externalDto = DigiSosResponseObjectMother.giveMeResponseSisteEndretEnUkeSiden()

        val internal = externalDto.toInternal()

        internal.shouldNotBeNull()
        internal.navn.value `should be equal to` externalDto.navn
        internal.kode.toString() `should be equal to` externalDto.kode
        internal.sistEndret?.toLocalDateTime() `should be equal to` externalDto.sistEndret
    }

    @Test
    fun `Skal kunne deserialisere en respons til en DigiSos-DTO`() {
        val deserialized = objectMapper.readValue<List<DigiSosResponse>>(responsFraDigiSos)

        deserialized.shouldNotBeNull()
    }

}
