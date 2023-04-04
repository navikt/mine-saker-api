package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.personbruker.minesaker.api.exception.TransformationException
import org.junit.jupiter.api.Test

internal class SakstemakodeTransformerTest {

    @Test
    fun `Skal takle at det kommer et ukjent tema`() {
        val externalUgylidgTema = "dummy"

        val res = runCatching {
            externalUgylidgTema.toInternalSaktemakode()
        }

        res.isFailure shouldBe true
        res.exceptionOrNull().shouldBeInstanceOf<TransformationException>()
        val exception = res.exceptionOrNull() as TransformationException
        exception.context["ukjentVerdi"] shouldBe externalUgylidgTema
    }

}
