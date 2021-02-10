package no.nav.personbruker.minesaker.api.saf

interface GraphQLRequest {
    val query: String
    val operationName : String?
        get() = null
    val variables: Map<String, Any>
        get() = emptyMap()

    fun String.compactJson(): String =
        trimIndent()
            .replace("\r", " ")
            .replace("\n", " ")
            .replace("\\s+".toRegex(), " ")

}
