package no.nav.personbruker.minesaker.api.saf.fullmakt

import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser

class FullmaktService(
    private val fullmaktConsumer: FullmaktConsumer,
    private val navnService: NavnService
) {
    suspend fun getFullmaktForhold(user: IdportenUser): FullmaktForhold {
        val fullmaktsGivere = fullmaktConsumer.getFullmaktsGivere(user)

        val navn = navnService.getNavn(user)

        return FullmaktForhold(
            navn = navn,
            ident = user.ident,
            fullmaktsGivere = fullmaktsGivere
        )
    }

    suspend fun validateFullmaktsGiver(user: IdportenUser, giverIdent: String): FullmaktGiver {
        val fullmaktsGivere = fullmaktConsumer.getFullmaktsGivere(user)

        return fullmaktsGivere.find { it.ident == giverIdent }
            ?: throw UgyldigFullmaktException("Manglende forhold", giver = giverIdent, fullmektig = user.ident)
    }

    suspend fun token(user: IdportenUser) = fullmaktConsumer.token(user)
}

data class FullmaktForhold(
    val navn: String,
    val ident: String,
    val fullmaktsGivere: List<FullmaktGiver>
)

data class FullmaktGiver(
    val ident: String,
    val navn: String
)

class UgyldigFullmaktException(
    override val message: String,
    val giver: String,
    val fullmektig: String
): IllegalArgumentException(message)
