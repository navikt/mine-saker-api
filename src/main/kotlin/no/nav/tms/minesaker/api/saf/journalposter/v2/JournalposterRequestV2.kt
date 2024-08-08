package no.nav.tms.minesaker.api.saf.journalposter.v2

import no.nav.dokument.saf.selvbetjening.generated.dto.HENT_JOURNALPOSTER_V2
import no.nav.tms.minesaker.api.saf.GraphQLRequest
import no.nav.tms.minesaker.api.saf.sakstemaer.Sakstemakode
import no.nav.tms.minesaker.api.saf.compactJson

class HentJournalposterV2Request(override val variables: HentJournalposterV2RequestVariables) : GraphQLRequest {

    override val query: String get() = queryString

    companion object {
        val queryString = compactJson(HENT_JOURNALPOSTER_V2)

        fun create(ident: String, sakstema: Sakstemakode) = HentJournalposterV2Request(
            HentJournalposterV2RequestVariables(ident, sakstema.name)
        )
    }
}

data class HentJournalposterV2RequestVariables(
    val ident: String,
    val sakstema: String

)

typealias SafAvsenderMottakerV2 = no.nav.dokument.saf.selvbetjening.generated.dto.hentjournalposterv2.AvsenderMottaker
typealias SafAvsenderMottakerIdTypeV2 = no.nav.dokument.saf.selvbetjening.generated.dto.enums.AvsenderMottakerIdType
typealias SafDatotypeV2 = no.nav.dokument.saf.selvbetjening.generated.dto.enums.Datotype
typealias SafDokumentInfoV2 = no.nav.dokument.saf.selvbetjening.generated.dto.hentjournalposterv2.DokumentInfo
typealias SafDokumentoversiktV2 = no.nav.dokument.saf.selvbetjening.generated.dto.hentjournalposterv2.Dokumentoversikt
typealias SafDokumentvariantV2 = no.nav.dokument.saf.selvbetjening.generated.dto.hentjournalposterv2.Dokumentvariant
typealias SafJournalpostV2 = no.nav.dokument.saf.selvbetjening.generated.dto.hentjournalposterv2.Journalpost
typealias SafJournalposttypeV2 = no.nav.dokument.saf.selvbetjening.generated.dto.enums.Journalposttype
typealias SafJournalstatusV2 = no.nav.dokument.saf.selvbetjening.generated.dto.enums.Journalstatus
typealias SafRelevantDatoV2 = no.nav.dokument.saf.selvbetjening.generated.dto.hentjournalposterv2.RelevantDato
typealias SafSakstemaV2 = no.nav.dokument.saf.selvbetjening.generated.dto.hentjournalposterv2.Sakstema
typealias SafTemaV2 = no.nav.dokument.saf.selvbetjening.generated.dto.enums.Tema
typealias SafVariantformatV2 = no.nav.dokument.saf.selvbetjening.generated.dto.enums.Variantformat
