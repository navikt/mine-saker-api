package no.nav.personbruker.minesaker.api.common.exception

import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

internal class UnknownValueExceptionTest {

    @Test
    fun `Skal inneholde info om feltet med ukjent verdi`() {
        val expectedFieldName = "forventetFeltnavn"

        val mfe = UnknownValueException(expectedFieldName)

        mfe.feltnavn `should be equal to` expectedFieldName
        mfe.toString().contains(expectedFieldName)
    }

}
