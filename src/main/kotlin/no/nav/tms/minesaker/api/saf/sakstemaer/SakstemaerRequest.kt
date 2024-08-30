package no.nav.tms.minesaker.api.saf.sakstemaer

import no.nav.dokument.saf.selvbetjening.generated.dto.HENT_SAKSTEMAER
import no.nav.dokument.saf.selvbetjening.generated.dto.HentSakstemaer
import no.nav.tms.minesaker.api.config.InnsynsUrlResolver
import no.nav.tms.minesaker.api.saf.GraphQLRequest
import no.nav.tms.minesaker.api.saf.compactJson

fun HentSakstemaer.Result.toInternal(innsynsUrlResolver: InnsynsUrlResolver): SakstemaResult =
    SakstemaResult(dokumentoversiktSelvbetjening.tema
        .map { externalTema -> externalTema.toInternal(innsynsUrlResolver) }
    )

class SakstemaerRequest(override val variables: Variables) : GraphQLRequest {

    override val query get() = queryString

    companion object {
        val queryString = compactJson(HENT_SAKSTEMAER)

        fun create(ident: String) = SakstemaerRequest (
            Variables(ident)
        )
    }
}

data class Variables(
    val ident: String
)

typealias SafDokumentoversikt = no.nav.dokument.saf.selvbetjening.generated.dto.hentsakstemaer.Dokumentoversikt
typealias SafJournalpost = no.nav.dokument.saf.selvbetjening.generated.dto.hentsakstemaer.Journalpost
typealias SafRelevantDato = no.nav.dokument.saf.selvbetjening.generated.dto.hentsakstemaer.RelevantDato
typealias SafSakstema = no.nav.dokument.saf.selvbetjening.generated.dto.hentsakstemaer.Sakstema
