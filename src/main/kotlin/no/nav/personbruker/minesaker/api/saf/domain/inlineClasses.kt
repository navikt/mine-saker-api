package no.nav.personbruker.minesaker.api.saf.domain

inline class Tittel(val value: String)

inline class Navn(val value: String)

inline class DokumentInfoId(val value: String) {
    override fun toString(): String {
        return value
    }
}

inline class JournalpostId(val value: String) {
    override fun toString(): String {
        return value
    }
}

inline class Fodselsnummer(val value: String) {
    override fun toString(): String {
        return "***"
    }
}
