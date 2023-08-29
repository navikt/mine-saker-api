package no.nav.personbruker.minesaker.api.saf.fullmakt

data class FullmaktDetails(
    val fullmaktsgiver: String,
    val fullmaktsgiverNavn: String
)

data class FullmaktForhold(
    val navn: String,
    val ident: String,
    val fullmaktsGivere: List<FullmaktsGiver>
)

data class FullmaktsGiver(
    val ident: String,
    val navn: String
)
