package no.nav.personbruker.minesaker.api.saf.domain

enum class Journalposttype(val beskrivelse: String) {
    INNGAAENDE("Inngående dokument"),
    UTGAAENDE("Utgående dokument"),
    NOTAT("Notat")
}
