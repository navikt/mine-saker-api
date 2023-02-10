package no.nav.personbruker.minesaker.api.common.exception

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test

internal class TransformationExceptionTest {

    @Test
    fun `ToString-metoden skal inneholde hvilken type feil det er`() {
        val expectedMessage = "Dummy feilmelding"
        val expectedErrorType = TransformationException.ErrorType.MISSING_FIELD

        val exception = TransformationException(expectedMessage, expectedErrorType)
        exception.addContext("dummyKey", "dummyValue")

        exception.type shouldBe expectedErrorType
        exception.message shouldBe expectedMessage
        println(exception.toString())
        exception.toString() shouldContain expectedErrorType.toString()
    }

    @Test
    fun `Hjelpemetoden for aa opprette TransformationException med feltnavn i konteksten`() {
        val expectedFieldName = "navnet på feltet er dette"

        val exception = TransformationException.withMissingFieldName(expectedFieldName)

        exception.type shouldBe TransformationException.ErrorType.MISSING_FIELD
        exception.context[TransformationException.feltnavnKey] shouldBe expectedFieldName
        exception.toString() shouldContain expectedFieldName
    }

}
