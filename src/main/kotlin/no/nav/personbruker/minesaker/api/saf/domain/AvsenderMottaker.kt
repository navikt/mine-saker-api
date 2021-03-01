package no.nav.personbruker.minesaker.api.saf.domain

import com.fasterxml.jackson.annotation.JsonIgnore

data class AvsenderMottaker(
    @JsonIgnore val id: String?,
    val type: AvsenderMottakerType
) {
    override fun toString(): String {
        return "AvsenderMottaker(id='***', type=$type)"
    }

}
