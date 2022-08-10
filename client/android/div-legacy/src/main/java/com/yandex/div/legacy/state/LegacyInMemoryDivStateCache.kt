package com.yandex.div.legacy.state

import androidx.annotation.MainThread

@MainThread
class LegacyInMemoryDivStateCache : LegacyDivStateCache {
    private val states = mutableMapOf<Pair<String, String>, String>()
    private val rootStates = mutableMapOf<String, String>()

    override fun putState(cardId: String, path: String, state: String) {
        states[cardId to path] = state
    }

    override fun putRootState(cardId: String, state: String) {
        rootStates[cardId] = state
    }

    override fun getState(cardId: String, path: String) = states[cardId to path]

    override fun getRootState(cardId: String) = rootStates[cardId]

    internal fun clear() {
        states.clear()
        rootStates.clear()
    }
}
