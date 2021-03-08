package no.nav.personbruker.minesaker.api.saf.domain

import com.fasterxml.jackson.annotation.JsonIgnore

data class AvsenderMottaker(
    @JsonIgnore val id: ID?,
    val type: AvsenderMottakerType
)
