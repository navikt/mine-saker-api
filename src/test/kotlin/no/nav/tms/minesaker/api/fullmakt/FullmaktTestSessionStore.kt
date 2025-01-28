package no.nav.tms.minesaker.api.fullmakt

import no.nav.tms.minesaker.api.fullmakt.FullmaktGiver
import no.nav.tms.minesaker.api.fullmakt.FullmaktSessionStore

class FullmaktTestSessionStore: FullmaktSessionStore {
    private val sessionMap = mutableMapOf<String, FullmaktGiver>()

    override suspend fun clearFullmaktGiver(ident: String) {
        sessionMap.remove(ident)
    }

    override suspend fun getCurrentFullmaktGiver(ident: String) = sessionMap[ident]

    override suspend fun setFullmaktGiver(ident: String, fullmaktGiver: FullmaktGiver) {
        sessionMap[ident] = fullmaktGiver
    }
}
