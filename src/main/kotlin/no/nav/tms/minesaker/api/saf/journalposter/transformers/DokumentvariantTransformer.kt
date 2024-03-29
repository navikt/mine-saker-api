package no.nav.tms.minesaker.api.saf.journalposter.transformers

import no.nav.dokument.saf.selvbetjening.generated.dto.enums.Variantformat
import no.nav.tms.minesaker.api.exception.TransformationException
import no.nav.tms.minesaker.api.domain.*

fun Variantformat.toInternal(): Dokumentvariant {
    return when (this) {
        Variantformat.ARKIV -> Dokumentvariant.ARKIV
        Variantformat.SLADDET -> Dokumentvariant.SLADDET
        else -> {
            val msg = "Klarte ikke å konvertere dokumentvariant"
            val exception = TransformationException(msg, TransformationException.ErrorType.INVALID_STATE)
            exception.addContext("funnetVariantformat", this)
            throw exception
        }
    }
}
