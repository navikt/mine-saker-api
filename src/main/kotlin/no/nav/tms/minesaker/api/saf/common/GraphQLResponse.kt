package no.nav.tms.minesaker.api.saf.common

import com.expediagroup.graphql.client.types.*

data class SafResponse<T>(
    override val data: T? = null,
    override val errors: List<SafError>? = null,
    override val extensions: Map<String, Any?>? = null
): GraphQLClientResponse<T>

data class SafError(
    override val message: String,
    override val locations: List<SafSourceLocation>? = null,
    override val extensions: Map<String, Any?>? = null,
    override val path: List<Any>? = null
): GraphQLClientError

data class SafSourceLocation(
    override val line: Int,
    override val column: Int
): GraphQLClientSourceLocation
