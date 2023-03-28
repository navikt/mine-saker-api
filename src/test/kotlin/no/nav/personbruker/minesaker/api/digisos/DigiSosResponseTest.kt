package no.nav.personbruker.minesaker.api.digisos

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import no.nav.personbruker.minesaker.api.config.InnsynsUrlResolver
import org.junit.jupiter.api.Test
import java.net.URL

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

    private val objectMapper = jacksonObjectMapper().apply {
        registerKotlinModule()
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }
    private val dummyResolver = InnsynsUrlResolver(mapOf(), URL("http://dummy.innsyn.no"))


    @Test
    fun `Skal kunne konvertere til intern type`() {
        val externalDto = DigiSosResponseObjectMother.giveMeResponseSisteEndretEnUkeSiden()

        val internal = externalDto.toInternal(dummyResolver)

        internal.shouldNotBeNull()
        internal.navn shouldBe externalDto.navn
        internal.kode.toString() shouldBe externalDto.kode
        internal.sistEndret?.toLocalDateTime() shouldBe externalDto.sistEndret
    }

    @Test
    fun `Skal kunne deserialisere en respons til en DigiSos-DTO`() {
        val deserialized = objectMapper.readValue<List<DigiSosResponse>>(responsFraDigiSos)

        deserialized.shouldNotBeNull()
    }

}
