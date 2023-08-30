package no.nav.personbruker.minesaker.api.saf.fullmakt

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.request.*
import io.ktor.http.*
import no.nav.pdl.generated.dto.HentNavn
import no.nav.pdl.generated.dto.hentnavn.Navn
import no.nav.personbruker.minesaker.api.config.TokendingsExchange
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser

class NavnConsumer(
    private val client: GraphQLKtorClient,
    private val pdlUrl: String,
    private val tokendingsExchange: TokendingsExchange
) {

    private val log = KotlinLogging.logger {}

    suspend fun fetchNavn(user: IdportenUser): String {
        val token = tokendingsExchange.pdlApiToken(user)
        val response: GraphQLClientResponse<HentNavn.Result> = sendQuery(user.ident, token)
        checkForErrors(response)
        return getNavnFromGraphQl(response).concatenateFull()
    }

    private suspend fun sendQuery(ident: String, token: String): GraphQLClientResponse<HentNavn.Result> {
        try {
            val hentNavnQuery = HentNavn(HentNavn.Variables(ident = ident))

            return client.execute(hentNavnQuery) {
                url(pdlUrl)
                header(HttpHeaders.Authorization, "Bearer $token")
                header("Tema", "GEN")
            }
        } catch (e: Exception) {
            throw QueryRequestException("Feil under sending av graphql spørringen", e)
        }
    }

    private fun checkForErrors(response: GraphQLClientResponse<HentNavn.Result>) {
        response.errors?.let { errors ->
            if (errors.isNotEmpty()) {
                log.warn { "Feil i GraphQL-responsen: $errors" }
                throw QueryResponseException("Feil i responsen under henting av navn")
            }
        }
    }
}
open class QueryRequestException(message: String, cause: Throwable) : Exception(message, cause)

class QueryResponseException(message: String) : Exception(message)

fun Navn.concatenateFull() = listOf(fornavn, mellomnavn, etternavn)
    .filter { navn -> !navn.isNullOrBlank() }
    .joinToString(" ")

fun getNavnFromGraphQl(result: GraphQLClientResponse<HentNavn.Result>) =
    result.data?.hentPerson?.navn?.first()
        ?: throw QueryResponseException("Klarte ikke hente navn.")
