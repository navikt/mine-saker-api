package no.nav.personbruker.minesaker.api.saf.fullmakt

data class FullmaktDetails(
    val fullmaktsgiver: String,
    val fullmaktsgiverNavn: String
)

data class FullmaktForhold(
    val ident: String,
    val fullmaktsGivere: List<FullmaktsGiver>
) {
    companion object {
        fun fromFullmaktDetails(ident: String, details: List<FullmaktDetails>) = FullmaktForhold(
            ident = ident,
            fullmaktsGivere = details
                .distinctBy { it.fullmaktsgiver }
                .map {
                    FullmaktsGiver(
                        ident = it.fullmaktsgiver,
                        navn = it.fullmaktsgiverNavn
                    )
            }
        )
    }
}

data class FullmaktsGiver(
    val ident: String,
    val navn: String
)
