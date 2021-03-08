package no.nav.personbruker.minesaker.api.saf.domain

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.common.exception.SafException
import no.nav.personbruker.minesaker.api.common.exception.UgyldigVerdiException
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be instance of`
import org.amshove.kluent.shouldBeEmpty
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

internal class SakstemakodeTest {

    private val log = LoggerFactory.getLogger(SakstemakodeTest::class.java)

    @Test
    fun `Skal takle at det kommer et ukjent tema`() {
        val externalUgylidgTema = "dummy"

        val res = runCatching {
            externalUgylidgTema.toInternalSaktemakode()
        }

        res.isFailure `should be equal to` true
        res.exceptionOrNull() `should be instance of` UgyldigVerdiException::class
        val exception = res.exceptionOrNull()
        exception as UgyldigVerdiException
        exception.context["ukjentVerdi"] `should be equal to` externalUgylidgTema
    }

    @Test
    fun `Skal inneholde alle kjente temakoder`() {
        val externalTemaer = HentJournalposter.Tema.values()
        val internalSakstemaer = Sakstemakode.values()

        val manglendeTemaer = mutableListOf<String>()
        externalTemaer.forEach { external ->

            runCatching {
                if (external.isTemaSomSkalRepresenteresInternt()) {
                    Sakstemakode.valueOf(external.toString())
                }

            }.onFailure {
                manglendeTemaer.add(external.toString())
            }
        }

        if (manglendeTemaer.isNotEmpty()) {
            log.info("manglendeTemaer: $manglendeTemaer")
        }
        manglendeTemaer.shouldBeEmpty()

        externalTemaer.size - 1 `should be equal to` internalSakstemaer.size
    }

    private fun HentJournalposter.Tema.isTemaSomSkalRepresenteresInternt() =
        this != HentJournalposter.Tema.__UNKNOWN_VALUE

}
