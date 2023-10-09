package no.nav.personbruker.minesaker.api.saf.fullmakt

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import com.github.benmanes.caffeine.cache.Caffeine
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import no.nav.pdl.generated.dto.HentNavn
import no.nav.pdl.generated.dto.hentnavn.Navn
import no.nav.personbruker.minesaker.api.config.TokendingsExchange
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser
import java.time.Duration

class NavnFetcher(
    private val navnConsumer: NavnConsumer
) {

    private val log = KotlinLogging.logger { }
    private val secureLog = KotlinLogging.logger("secureLog")

    constructor(
        client: GraphQLKtorClient,
        pdlUrl: String,
        tokendingsExchange: TokendingsExchange
    ): this(NavnConsumer(client, pdlUrl, tokendingsExchange))

    private val cache = Caffeine.newBuilder()
        .maximumSize(1000)
        .expireAfterWrite(Duration.ofMinutes(5))
        .build<String, String>()

    fun getNavn(user: IdportenUser) = try {
        cache.get(user.ident) { _ ->
            navnConsumer.fetchNavn(user)
        }
    } catch (e: Exception) {
        log.warn { "Feil ved henting av navn" }
        secureLog.warn(e) { "Feil ved henting av navn for bruker ${user.ident}" }
        user.ident
    }
}

class NavnConsumer(
    private val client: GraphQLKtorClient,
    private val pdlUrl: String,
    private val tokendingsExchange: TokendingsExchange
) {
    private val log = KotlinLogging.logger {}
    private val secureLog = KotlinLogging.logger("secureLog")

    fun fetchNavn(user: IdportenUser) = runBlocking {
        val token = tokendingsExchange.pdlApiToken(user)
        sendQuery(user.ident, token)
            .also { checkForErrors(it) }
            .let { unpackNavn(it) }
            .let { concatenate(it) }
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
                log.warn { "Feil i GraphQL-respons fra pdl-api." }
                secureLog.warn { "Feil i GraphQL-respons fra pdl-api: $errors" }
                throw QueryResponseException("Feil i respons ved henting av navn")
            }
        }
    }

    private fun concatenate(navn: Navn) = listOf(navn.fornavn, navn.mellomnavn, navn.etternavn)
        .filter { !it.isNullOrBlank() }
        .joinToString(" ")

    private fun unpackNavn(result: GraphQLClientResponse<HentNavn.Result>) =
        result.data?.hentPerson?.navn?.first()
            ?: throw QueryResponseException("Klarte ikke hente navn.")

}
class QueryRequestException(message: String, cause: Throwable) : Exception(message, cause)

class QueryResponseException(message: String) : Exception(message)
