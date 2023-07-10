package no.nav.personbruker.minesaker.api.saf.fullmakt

import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser

class FullmaktService(private val fullmaktConsumer: FullmaktConsumer) {
    suspend fun getFullmaktForhold(user: IdportenUser): FullmaktForhold {
        return fullmaktConsumer.getFullmaktForhold(user)
    }

    suspend fun validateFullmaktsForhold(user: IdportenUser, giverIdent: String) {
        val alleForhold = fullmaktConsumer.getFullmaktForhold(user)

        alleForhold.fullmaktsGivere.find { it.ident == giverIdent }
            ?: throw UgyldigFullmaktException("Manglende forhold", giver = giverIdent, fullmektig = user.ident)

    }
}

class UgyldigFullmaktException(
    override val message: String,
    val giver: String,
    val fullmektig: String
): IllegalArgumentException(message)
