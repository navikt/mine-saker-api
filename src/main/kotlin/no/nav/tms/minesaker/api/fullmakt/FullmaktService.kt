package no.nav.tms.minesaker.api.fullmakt

import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser

class FullmaktService(
    private val fullmaktConsumer: FullmaktConsumer,
    private val navnFetcher: NavnFetcher
) {
    suspend fun getFullmaktForhold(user: IdportenUser) =
        FullmaktForhold(
            navn = navnFetcher.getNavn(user),
            ident = user.ident,
            fullmaktsGivere = fullmaktConsumer.getFullmaktsGivere(user)
        )

    suspend fun validateFullmaktsGiver(user: IdportenUser, giverIdent: String) =
        fullmaktConsumer.getFullmaktsGivere(user)
            .find { it.ident == giverIdent }
            ?: throw UgyldigFullmaktException("Manglende forhold", giver = giverIdent, fullmektig = user.ident)

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
