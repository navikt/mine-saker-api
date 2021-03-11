package no.nav.personbruker.minesaker.api.tokenx

inline class AccessToken(val value: String) {
    override fun toString(): String {
        return value
    }
}
