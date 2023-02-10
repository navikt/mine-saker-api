package no.nav.personbruker.minesaker.api.common.exception

import com.expediagroup.graphql.client.types.GraphQLClientError
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test

internal class GraphQLResultExceptionTest {

    @Test
    fun `ToString-metoden skal inneholde exception-spesifikke verdier`() {
        val expectedMessage = "Dummy message"
        val expectedError = object : GraphQLClientError { override val message = "Valideringsfeil" }
        val expectedErrors = listOf(expectedError)
        val expectedExtensions: Map<String, String> = mapOf("key" to "value")

        val gre = GraphQLResultException(expectedMessage, expectedErrors, expectedExtensions)

        gre.toString() shouldContain expectedMessage
        gre.toString() shouldContain expectedErrors[0].toString()
        gre.toString() shouldContain "value"
    }

}
