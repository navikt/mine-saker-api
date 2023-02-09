package no.nav.personbruker.minesaker.api.saf.journalposter.objectmothers

import no.nav.dokument.saf.selvbetjening.generated.dto.HentJournalposter
import no.nav.personbruker.minesaker.api.saf.common.GraphQLError
import no.nav.personbruker.minesaker.api.saf.common.GraphQLResponse
import no.nav.personbruker.minesaker.api.saf.journalposter.HentJournalpostResultObjectMother

object HentJournalposterResultObjectMother {

    fun giveMeOneResult(): GraphQLResponse<HentJournalposter.Result> {
        val data = HentJournalpostResultObjectMother.giveMeHentJournalposterResult()
        return GraphQLResponse(data)
    }

    fun giveMeResponseWithError(data: HentJournalposter.Result? = null): GraphQLResponse<HentJournalposter.Result> {
        val error = GraphQLError("Feilet ved henting av data for bruker.")

        return GraphQLResponse(data, listOf(error))
    }

    fun giveMeResponseWithDataAndError(): GraphQLResponse<HentJournalposter.Result> {
        val data = HentJournalpostResultObjectMother.giveMeHentJournalposterResult()
        return giveMeResponseWithError(data)
    }

}
