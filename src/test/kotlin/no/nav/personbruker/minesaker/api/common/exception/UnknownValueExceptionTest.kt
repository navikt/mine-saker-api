package no.nav.personbruker.minesaker.api.common.exception

import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should have key`
import org.junit.jupiter.api.Test

internal class UnknownValueExceptionTest {

    @Test
    fun `Skal inneholde info om feltet med ukjent verdi`() {
        val expectedFieldKey = "feltnavn"
        val expectedFieldValue = "forventetFeltnavn"

        val mfe = UnknownValueException(expectedFieldValue)

        mfe.context.`should have key`(expectedFieldKey)
        mfe.context[expectedFieldKey] `should be equal to` expectedFieldValue
        mfe.toString().contains(expectedFieldKey)
        mfe.toString().contains(expectedFieldValue)
    }

}