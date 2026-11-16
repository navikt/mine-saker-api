package no.nav.tms.minesaker.api.representasjon

fun reprTestResponse(representant: String, verger: List<String>, fullmaktsgivere: List<String>) = """
{
    "fullmakt": [${fullmaktsgivere.joinToString { "{ \"fullmaktsgiver\": \"$it\"}" }}],
    "vergemaal": [${verger.joinToString { "{ \"vergehaver\": \"$it\", \"verge\": \"$representant\"}" }}]
}
"""

data class Person(
    val ident: String,
    val navn: String,
) {
    val fornavn: String = navn.split(" ").first()
    val mellomnavn: String? = navn.split(" ").drop(1).dropLast(1).firstOrNull()
    val etternavn: String? = navn.split(" ").drop(1).lastOrNull()
}

fun hentPersonTestResponse(person: Person) = """
{
    "data": {
        "hentPerson": {
            "navn": [
                {
                    "fornavn": "${person.fornavn}",
                    "mellomnavn": ${person.mellomnavn?.let {"\"$it\""}},
                    "etternavn": ${person.etternavn?.let {"\"$it\""}}   
                }
            ] 
        }
    }
}
"""

fun hentPersonBolkTestResponse(personer: List<Person>) = """
{
    "data": {
        "hentPersonBolk": [
            ${personer.joinToString(transform = ::bolkPerson)}        
        ]
    }
}
"""

private fun bolkPerson(person: Person): String = """
{
    "ident": "${person.ident}",
    "code": "ok",
    "person": {
        "navn": [
            {
                "fornavn": "${person.fornavn}",
                "mellomnavn": ${person.mellomnavn?.let {"\"$it\""}},
                "etternavn": ${person.etternavn?.let {"\"$it\""}} 
            }
        ]
    }
}
"""
