package no.nav.personbruker.minesaker.api.common.exception

import org.junit.jupiter.api.Test

internal class MissingFieldExceptionTest {

    @Test
    fun `Skal inneholde info om feltet som manglet`() {
        val expectedField = "forventetFeltnavn"

        val mfe = MissingFieldException(expectedField)

        mfe.toString().contains(expectedField)
    }

}
