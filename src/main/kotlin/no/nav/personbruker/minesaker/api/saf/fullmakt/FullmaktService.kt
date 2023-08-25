package no.nav.personbruker.minesaker.api.saf.fullmakt

import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser

class FullmaktService(private val fullmaktConsumer: FullmaktConsumer) {
    suspend fun getFullmaktForhold(user: IdportenUser): FullmaktForhold {
        return fullmaktConsumer.getFullmaktForhold(user)
    }

    suspend fun validateFullmaktsForhold(user: IdportenUser, giverIdent: String): ValidForhold {
        val alleForhold = fullmaktConsumer.getFullmaktForhold(user)

        val foundForhold = alleForhold.fullmaktsGivere.find { it.ident == giverIdent }
            ?: throw UgyldigFullmaktException("Manglende forhold", giver = giverIdent, fullmektig = user.ident)

        return ValidForhold(
            fullmektigIdent = alleForhold.ident,
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
) {
    val fullmektigNavn = "N/A"
}

class UgyldigFullmaktException(
    override val message: String,
    val giver: String,
    val fullmektig: String
): IllegalArgumentException(message)
