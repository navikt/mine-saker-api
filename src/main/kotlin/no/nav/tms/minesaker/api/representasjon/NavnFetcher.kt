package no.nav.tms.minesaker.api.representasjon

import com.github.benmanes.caffeine.cache.Caffeine
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import no.nav.tms.common.logging.TeamLogs
import no.nav.tms.minesaker.api.setup.SystemTokenFetcher
import no.nav.tms.minesaker.api.setup.TokendingsExchange
import no.nav.tms.token.support.user.token.verification.UserPrincipal
import java.time.Duration

class NavnFetcher(
    private val client: HttpClient,
    private val pdlUrl: String,
    private val pdlBehandlingsnummer: String,
    private val tokendingsExchange: TokendingsExchange,
    private val systemTokenFetcher: SystemTokenFetcher
) {

    private val cache = Caffeine.newBuilder()
        .maximumSize(5000)
        .expireAfterWrite(Duration.ofMinutes(60))
        .build<String, String>()

    private val log = KotlinLogging.logger {}
    private val teamLog = TeamLogs.logger { }

    fun getNavn(user: UserPrincipal): String {
        return cache.get(user.ident) {
            fetchNavnForUser(user)
        }
    }

    fun getNavn(identer: List<String>): Map<String, String> {
        val distinct = identer.distinct()

        val cachedeNavn = cache.getAllPresent(distinct)

        if (distinct.size == cachedeNavn.size) {
            return cachedeNavn
        } else {
            return fetchNavnAndUpdateCache(distinct, cachedeNavn)
        }
    }

    fun getNavn(ident: String): String {
        return cache.get(ident) {
            runBlocking {
                queryForNavn(ident, systemTokenFetcher.pdlApiToken())
                    .let { checkForErrors(it) }
                    .hentPerson
                    .fullnavn
            }
        }
    }

    private fun fetchNavnForUser(user: UserPrincipal): String = runBlocking(Dispatchers.IO) {
        tokendingsExchange.pdlApiToken(user.accessToken)
            .let { token -> queryForNavn(user.ident, token) }
            .let { response -> checkForErrors(response) }
            .hentPerson.fullnavn
    }

    private suspend fun queryForNavn(ident: String, token: String): HentNavnResponse {
        val response = client.post {
            url(pdlUrl)
            header(HttpHeaders.Authorization, "Bearer $token")
            header("Behandlingsnummer", pdlBehandlingsnummer)
            header("Tema", "GEN")
            contentType(ContentType.Application.Json)
            setBody(HentNavn(ident))
        }

        if (!response.status.isSuccess()) {
            throw HentNavnException("Fikk http-feil fra PDL")
        }

        return try {
            response.body()
        } catch (e: Exception) {
            teamLog.error(e) { "Klarer ikke tolke svar fra PDL." }
            throw HentNavnException("Klarte ikke tolke svar fra PDL", e)
        }
    }

    private fun fetchNavnAndUpdateCache(
        distinct: List<String>,
        cachedeNavn: Map<String, String>
    ): Map<String, String> = runBlocking(Dispatchers.IO) {
        val missing = distinct - cachedeNavn.keys

        val response = queryForNavnBolk(missing, systemTokenFetcher.pdlApiToken())

        val identNavn = response.data
            ?.hentPersonBolk
            ?.filter {
                it.responseCode == HentNavnBolkResponse.ResponseCode.Ok
            }?.map {
                it.ident to it.fullnavn
            } ?: emptyList()

        identNavn.forEach { (ident, navn) ->
            cache.put(ident, navn)
        }

        identNavn.toMap() + cachedeNavn
    }

    private suspend fun queryForNavnBolk(identer: List<String>, token: String): HentNavnBolkResponse {
        val response = client.post {
            url(pdlUrl)
            header(HttpHeaders.Authorization, "Bearer $token")
            header("Behandlingsnummer", pdlBehandlingsnummer)
            header("Tema", "GEN")
            contentType(ContentType.Application.Json)
            setBody(HentNavnBolk(identer))
        }

        if (!response.status.isSuccess()) {
            throw HentNavnException("Fikk http-feil fra PDL")
        }

        return try {
            response.body()
        } catch (e: Exception) {
            teamLog.error(e) { "Klarer ikke tolke svar fra PDL." }
            throw HentNavnException("Klarte ikke tolke svar fra PDL", e)
        }
    }

    private fun checkForErrors(response: HentNavnResponse): HentNavnResponse.HentNavnData {

        response.errors?.let { errors ->
            if (errors.isNotEmpty()) {
                log.warn { "Feil i GraphQL-responsen: $errors" }
                throw HentNavnException("Feil i responsen under henting av navn")
            }
        }

        return response.data?: throw HentNavnException("Ingen data i graphql-svar.")
    }
}

class HentNavnException(message: String, cause: Exception? = null): Exception(message, cause)

private class HentNavn(ident: String) {
    val query = """
        query(${'$'}ident: ID!) {
            hentPerson(ident: ${'$'}ident) {
                navn {
                    fornavn,
                    mellomnavn,
                    etternavn
                }
            }
        }
    """.compactJson()

    val variables = mapOf(
        "ident" to ident
    )
}

private class HentNavnBolk(identer: List<String>) {
    val query = """
        query(${'$'}identer: [ID!]!) {
            hentPersonBolk(identer: ${'$'}identer) {
                ident,
                code,
                person {
                    navn {
                        fornavn,
                        mellomnavn,
                        etternavn
                    }
                }
            }
        }
    """.compactJson()

    val variables = mapOf(
        "identer" to identer
    )
}

fun String.compactJson(): String =
    trimIndent()
        .replace("\r", " ")
        .replace("\n", " ")
        .replace("\\s+".toRegex(), " ")

private data class HentNavnResponse(
    val data: HentNavnData?,
    val errors: List<Map<String, Any>>?
) {
    data class HentNavnData(
        val hentPerson: Person
    )

    data class Person (
        val navn: List<Navn>
    ) {
        val fullnavn = navn.first().let {
            listOf(it.fornavn, it.mellomnavn, it.etternavn)
                .filter { navn -> !navn.isNullOrBlank() }
                .joinToString(" ")
        }
    }

    data class Navn(
        val fornavn: String,
        val mellomnavn: String? = null,
        val etternavn: String,
    )
}

private data class HentNavnBolkResponse(
    val data: HentNavnData?,
    val errors: List<Map<String, Any>>?
) {
    data class HentNavnData(
        val hentPersonBolk: List<Person>
    )

    data class Person (
        val ident: String,
        private val code: String,
        private val person: PersonNavn?
    ) {
        val responseCode = ResponseCode.entries.find { it.code == code }
        val fullnavn = if (person == null) {
            ident
        } else {
            person.navn.first().let {
                listOf(it.fornavn, it.mellomnavn, it.etternavn)
                    .filter { navn -> !navn.isNullOrBlank() }
                    .joinToString(" ")
            }
        }
    }

    data class PersonNavn(
        val navn: List<Navn>
    )

    enum class ResponseCode(val code: String) {
        Ok("ok"),
        BadRequest("bad_request"),
        NotFound("not_found")
    }

    data class Navn(
        val fornavn: String,
        val mellomnavn: String? = null,
        val etternavn: String,
    )
}

