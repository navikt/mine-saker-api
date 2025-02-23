package no.nav.tms.minesaker.api.journalpost.query

import com.expediagroup.graphql.client.types.*

data class GraphQLResponse<T>(
    override val data: T? = null,
    override val errors: List<GraphQLError>? = null,
    override val extensions: Map<String, Any?>? = null
): GraphQLClientResponse<T>

data class GraphQLError(
    override val message: String,
    override val locations: List<GraphQLSourceLocation>? = null,
    override val extensions: Map<String, Any?>? = null,
    override val path: List<Any>? = null
): GraphQLClientError

data class GraphQLSourceLocation(
    override val line: Int,
    override val column: Int
): GraphQLClientSourceLocation
