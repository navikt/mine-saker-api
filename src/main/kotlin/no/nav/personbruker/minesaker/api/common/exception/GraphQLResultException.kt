package no.nav.personbruker.minesaker.api.common.exception

import com.expediagroup.graphql.types.GraphQLError

open class GraphQLResultException(
    message: String,
    internal val errors: List<GraphQLError>?,
    internal val extensions: Map<Any, Any>?
) : AbstractMineSakerException(message) {

    override fun toString(): String {
        return "${super.toString()}, errors=$errors, extensions=$extensions)"
    }

}
