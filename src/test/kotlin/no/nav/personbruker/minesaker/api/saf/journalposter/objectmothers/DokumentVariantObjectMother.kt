package no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers

import no.nav.personbruker.minesaker.api.saf.journalposter.transformers.GraphQLDokumentvariant
import no.nav.personbruker.minesaker.api.saf.journalposter.transformers.GraphQLVariantformat

object DokumentVariantObjectMother {

    private val ingenFeilkode = listOf("ok")

    fun giveMeArkivertVariant(): GraphQLDokumentvariant {
        return GraphQLDokumentvariant(GraphQLVariantformat.ARKIV, true, ingenFeilkode)
    }

    fun giveMeSladdetVariant(): GraphQLDokumentvariant {
        val feilkode = listOf("Skannet_dokument")
        return GraphQLDokumentvariant(GraphQLVariantformat.SLADDET, true, feilkode)
    }

}
