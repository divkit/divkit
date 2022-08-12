package com.yandex.div.state

import androidx.annotation.AnyThread
import java.util.Collections

@AnyThread
class InMemoryDivStateCache : DivStateCache {
    private val states = Collections.synchronizedMap(mutableMapOf<Pair<String, String>, String>())
    private val rootStates = Collections.synchronizedMap(mutableMapOf<String, String>())

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
