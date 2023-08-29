package no.nav.personbruker.minesaker.api.saf.fullmakt

import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser

class FullmaktService(
    private val fullmaktConsumer: FullmaktConsumer,
    private val navnConsumer: NavnConsumer
) {
    suspend fun getFullmaktForhold(user: IdportenUser): FullmaktForhold {
        val fullmaktsGivere = fullmaktConsumer.getFullmaktsGivere(user)

        val navn = navnConsumer.fetchNavn(user)

        return FullmaktForhold(
            navn = navn,
            ident = user.ident,
            fullmaktsGivere = fullmaktsGivere
        )
    }

    suspend fun validateFullmaktsForhold(user: IdportenUser, giverIdent: String): ValidForhold {
        val fullmaktsGivere = fullmaktConsumer.getFullmaktsGivere(user)

        val foundForhold = fullmaktsGivere.find { it.ident == giverIdent }
            ?: throw UgyldigFullmaktException("Manglende forhold", giver = giverIdent, fullmektig = user.ident)

        return ValidForhold(
            fullmektigIdent = user.ident,
            representertIdent = foundForhold.ident,
            representertNavn = foundForhold.navn
        )
    }

    suspend fun token(user: IdportenUser) = fullmaktConsumer.token(user)
}

data class ValidForhold(
    val fullmektigIdent: String,
    val representertIdent: String,
    val representertNavn: String,
)

class UgyldigFullmaktException(
    override val message: String,
    val giver: String,
    val fullmektig: String
): IllegalArgumentException(message)
