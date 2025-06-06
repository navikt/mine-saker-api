package no.nav.tms.minesaker.api.journalpost.query

interface GraphQLRequest {
    val query: String
    val operationName : String?
        get() = null
    val variables: Any
}


fun compactJson(json: String): String =
    json.trimIndent()
        .replace("\r", " ")
        .replace("\n", " ")
        .replace("\\s+".toRegex(), " ")
