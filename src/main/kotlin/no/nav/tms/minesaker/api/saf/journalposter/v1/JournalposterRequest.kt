package no.nav.tms.minesaker.api.saf.journalposter.v1

import no.nav.dokument.saf.selvbetjening.generated.dto.HENT_JOURNALPOSTER
import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.tms.minesaker.api.saf.GraphQLRequest
import no.nav.tms.minesaker.api.saf.compactJson
import no.nav.tms.minesaker.api.saf.sakstemaer.Sakstemakode

class JournalposterRequest(override val variables: JournalposterRequestVariables) : GraphQLRequest {

    override val query: String get() = queryString

    companion object {
        val queryString = compactJson(HENT_JOURNALPOSTER)

        fun create(ident: String, tema: Sakstemakode) = JournalposterRequest(
            JournalposterRequestVariables(ident, tema.name)
        )
    }
}

data class JournalposterRequestVariables(
    val ident: String,
    val temaetSomSkalHentes: String
)

fun HentJournalposter.Result.toInternal(innloggetBruker: String): JournalposterResponse? =
    dokumentoversiktSelvbetjening
        .tema
        .map {
                externalTema -> externalTema.toInternal(innloggetBruker)
        }.firstOrNull()


typealias SafAvsenderMottaker = no.nav.dokument.saf.selvbetjening.generated.dto.hentjournalposter.AvsenderMottaker
typealias SafAvsenderMottakerIdType = no.nav.dokument.saf.selvbetjening.generated.dto.enums.AvsenderMottakerIdType
typealias SafDatotype = no.nav.dokument.saf.selvbetjening.generated.dto.enums.Datotype
typealias SafDokumentInfo = no.nav.dokument.saf.selvbetjening.generated.dto.hentjournalposter.DokumentInfo
typealias SafDokumentoversikt = no.nav.dokument.saf.selvbetjening.generated.dto.hentjournalposter.Dokumentoversikt
typealias SafDokumentvariant = no.nav.dokument.saf.selvbetjening.generated.dto.hentjournalposter.Dokumentvariant
typealias SafJournalpost = no.nav.dokument.saf.selvbetjening.generated.dto.hentjournalposter.Journalpost
typealias SafJournalposttype = no.nav.dokument.saf.selvbetjening.generated.dto.enums.Journalposttype
typealias SafRelevantDato = no.nav.dokument.saf.selvbetjening.generated.dto.hentjournalposter.RelevantDato
typealias SafSakstema = no.nav.dokument.saf.selvbetjening.generated.dto.hentjournalposter.Sakstema
typealias SafTema = no.nav.dokument.saf.selvbetjening.generated.dto.enums.Tema
typealias SafVariantformat = no.nav.dokument.saf.selvbetjening.generated.dto.enums.Variantformat
