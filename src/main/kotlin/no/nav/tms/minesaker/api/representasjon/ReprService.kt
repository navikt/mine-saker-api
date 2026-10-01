package no.nav.tms.minesaker.api.representasjon

import no.nav.tms.token.support.user.token.verification.UserPrincipal

class ReprService(
    private val reprConsumer: ReprConsumer,
    private val navnFetcher: NavnFetcher
) {
    suspend fun getFullmaktForhold(user: UserPrincipal): Representantforhold {
        val kanRepresentere = reprConsumer.getRepresenterte(user)

        val navn = getNavn(kanRepresentere)

        val fullmaktsGivere = kanRepresentere.fullmakt.map {
            Representert(
                navn = navn[it.fullmaktsgiver] ?: it.fullmaktsgiver,
                ident = it.fullmaktsgiver,
                type = Representasjonstype.Fullmakt
            )
        }

        val verger = kanRepresentere.vergemaal.map {
            Representert(
                navn = navn[it.verge] ?: it.verge,
                ident = it.verge,
                type = Representasjonstype.Verge
            )
        }

        return Representantforhold(
            navn = navnFetcher.getNavn(user),
            ident = user.ident,
            fullmaktsGivere = fullmaktsGivere,
            verger = verger
        )
    }

    suspend fun validateForhold(user: UserPrincipal, giverIdent: String, type: Representasjonstype): Representert {
        val kanRepresentere = reprConsumer.getRepresenterte(user)

        return if (type == Representasjonstype.Fullmakt) {
            kanRepresentere.fullmakt.find { it.fullmaktsgiver == giverIdent }
                ?.let {
                    Representert(
                        navn = navnFetcher.getNavn(giverIdent),
                        ident = giverIdent,
                        type = Representasjonstype.Fullmakt

                    )
                } ?: throw UgyldigFullmaktException("Manglende forhold", giver = giverIdent, fullmektig = user.ident)
        } else {
            kanRepresentere.vergemaal.find { it.verge == giverIdent }
                ?.let {
                    Representert(
                        navn = navnFetcher.getNavn(giverIdent),
                        ident = giverIdent,
                        type = Representasjonstype.Verge

                    )
                } ?: throw UgyldigFullmaktException("Manglende forhold", giver = giverIdent, fullmektig = user.ident)
        }
    }

    private fun getNavn(kanRepresentere: KanRepresentere): Map<String, String> {
        val identer = kanRepresentere.run {
            fullmakt.map { it.fullmaktsgiver } + vergemaal.map { it.verge }
        }

        return navnFetcher.getNavn(identer)
    }

    suspend fun token(user: UserPrincipal) = reprConsumer.token(user)
}

data class Representantforhold(
    val navn: String,
    val ident: String,
    val fullmaktsGivere: List<Representert>,
    val verger: List<Representert>
)

data class Representert(
    val navn: String,
    val ident: String,
    val type: Representasjonstype
)

enum class Representasjonstype {
    Fullmakt, Verge
}

class UgyldigFullmaktException(
    override val message: String,
    val giver: String,
    val fullmektig: String
): IllegalArgumentException(message)
