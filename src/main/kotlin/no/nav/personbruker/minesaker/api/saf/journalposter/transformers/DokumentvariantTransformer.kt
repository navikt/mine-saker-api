package no.nav.personbruker.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter.Variantformat.ARKIV
import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter.Variantformat.SLADDET
import no.nav.personbruker.minesaker.api.common.exception.TransformationException
import no.nav.personbruker.minesaker.api.domain.*
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger(HentJournalposter.Variantformat::class.java)

fun HentJournalposter.Variantformat.toInternal(): Dokumentvariant {
    return when (this) {
        ARKIV -> Dokumentvariant.ARKIV
        SLADDET -> Dokumentvariant.SLADDET
        else -> {
            val msg = "Klarte ikke å konvertere dokumentvariant"
            val exception = TransformationException(msg, TransformationException.ErrorType.INVALID_STATE)
            exception.addContext("funnetVariantformat", this)
            throw exception
        }
    }
}
