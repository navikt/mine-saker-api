package no.nav.personbruker.minesaker.api.saf.dto.`in`

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test

internal class SafResultWrapperTest {

    private val objectMapper = jacksonObjectMapper()

    private val exampleResponseFromSaf = """
        {
          "data": {
            "dokumentoversiktSelvbetjening": {
              "tema": [
                {
                  "navn": "Foreldrepenger",
                  "kode": "FOR"
                }
              ]
            }
          }
        }
    """

    @Test
    fun `Skal kunne deserialisere en reell respons fra SAF`() {
        val deserializedToDto = objectMapper.readValue<SafResultWrapper>(exampleResponseFromSaf)

        deserializedToDto.shouldNotBeNull()
    }

}
