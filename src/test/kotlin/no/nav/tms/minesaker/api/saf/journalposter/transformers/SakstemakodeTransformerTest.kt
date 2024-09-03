package no.nav.tms.minesaker.api.saf.journalposter.transformers

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.tms.minesaker.api.saf.sakstemaer.SakstemaException
import no.nav.tms.minesaker.api.saf.sakstemaer.toInternalSaktemakode
import org.junit.jupiter.api.Test

internal class SakstemakodeTransformerTest {

    @Test
    fun `Skal takle at det kommer et ukjent tema`() {
        val externalUgylidgTema = "dummy"

        val res = runCatching {
            externalUgylidgTema.toInternalSaktemakode()
        }

        res.isFailure shouldBe true
        res.exceptionOrNull().shouldBeInstanceOf<SakstemaException>()
        val exception = res.exceptionOrNull() as SakstemaException
        exception.context["ukjentVerdi"] shouldBe externalUgylidgTema
    }

}
