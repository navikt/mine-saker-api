package no.nav.personbruker.minesaker.api.saf.fullmakt

import java.time.Instant
import java.time.LocalDate

data class FullmaktDetails(
    val fullmaktId: Long,
    val registrert: Instant,
    val registrertAv: String,
    val endret: Instant,
    val ednretAv: String,
    val opphoert: Boolean,
    val fullmaktsgiver: String,
    val fullmektig: String,
    val omraade: String,
    val gyldigFraOgMed: LocalDate,
    val gyldigTilOgMed: LocalDate,
    val fullmaktUuid: String,
    val opplysningsId: String,
    val endringsId: Long,
    val status: String,
    val fullmaktsgiverNavn: String,
    val fullmektigsNavn: String
)

data class FullmaktForhold(
    val navn: String,
    val ident: String,
    val fullmaktsGivere: List<FullmaktsGiver>
) {
    companion object {
        fun fromFullmaktDetails(details: List<FullmaktDetails>) = FullmaktForhold(
            navn = details.firstOrNull()?.fullmektigsNavn ?: "N/A",
            ident = details.firstOrNull()?.fullmektig ?: "N/A",
            fullmaktsGivere = details.map {
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
