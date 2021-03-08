package no.nav.personbruker.minesaker.api.saf.domain

inline class Tittel(val value: String)

inline class Navn(val value: String)

inline class FilUUID(val value: String)

inline class JournalpostId(val value: String)

inline class ID(val value: String) {
    override fun toString(): String {
        return "***"
    }
}
