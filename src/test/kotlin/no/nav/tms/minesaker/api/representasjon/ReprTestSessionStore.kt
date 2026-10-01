package no.nav.tms.minesaker.api.representasjon

class ReprTestSessionStore: ReprSessionStore {
    private val sessionMap = mutableMapOf<String, Representert>()

    override suspend fun clearRepresentert(ident: String) {
        sessionMap.remove(ident)
    }

    override suspend fun getCurrentRepresentert(ident: String) = sessionMap[ident]

    override suspend fun setRepresentert(ident: String, representert: Representert) {
        sessionMap[ident] = representert
    }
}
