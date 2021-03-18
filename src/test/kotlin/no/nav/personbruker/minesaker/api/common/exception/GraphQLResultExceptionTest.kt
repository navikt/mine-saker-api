package no.nav.personbruker.minesaker.api.common.exception

import com.expediagroup.graphql.types.GraphQLError
import org.amshove.kluent.`should contain`
import org.junit.jupiter.api.Test

internal class GraphQLResultExceptionTest {

    @Test
    fun `ToString-metoden skal inneholde exception-spesifikke verdier`() {
        val expectedMessage = "Dummy message"
        val expectedErrors = listOf(GraphQLError("Valideringsfeil"))
        val expectedExtensions: Map<Any, String> = mapOf("key" to "value")

        val gre = GraphQLResultException(expectedMessage, expectedErrors, expectedExtensions)

        gre.toString() `should contain` expectedMessage
        gre.toString() `should contain` expectedErrors[0].toString()
        gre.toString() `should contain` expectedExtensions[0].toString()
    }

}
