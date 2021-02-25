package no.nav.personbruker.minesaker.api.saf.domain

data class AvsenderMottaker(
    val id: String?,
    val type: AvsenderMottakerType
) {
    override fun toString(): String {
        return "AvsenderMottaker(id='***', type=$type)"
    }

}
