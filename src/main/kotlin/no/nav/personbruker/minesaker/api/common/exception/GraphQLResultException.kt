package no.nav.personbruker.minesaker.api.common.exception

import com.expediagroup.graphql.client.types.GraphQLClientError

open class GraphQLResultException(
    message: String,
    internal val errors: List<GraphQLClientError>?,
    internal val extensions: Map<String, Any?>?
) : AbstractMineSakerException(message) {

    override fun toString(): String {
        return "${super.toString()}, errors=$errors, extensions=$extensions)"
    }

}
