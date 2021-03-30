package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.personbruker.minesaker.api.common.exception.TransformationException
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be instance of`
import org.junit.jupiter.api.Test

internal class SakstemakodeTransformerTest {

    @Test
    fun `Skal takle at det kommer et ukjent tema`() {
        val externalUgylidgTema = "dummy"

        val res = runCatching {
            externalUgylidgTema.toInternalSaktemakode()
        }

        res.isFailure `should be equal to` true
        res.exceptionOrNull() `should be instance of` TransformationException::class
        val exception = res.exceptionOrNull() as TransformationException
        exception.context["ukjentVerdi"] `should be equal to` externalUgylidgTema
    }

}
